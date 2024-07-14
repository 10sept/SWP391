package Dal;

import Model.Feedback;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedbackDao extends DBContext {

    public List<Feedback> getFeedbackByPid(int pid) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT f.id, f.userId, f.pid, f.comment, f.rate, f.createdAt, u.fullName "
                + "FROM Feedback f "
                + "JOIN Users u ON f.userId = u.id "
                + "WHERE f.pid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pid);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setId(rs.getInt("id"));
                    feedback.setUserID(rs.getInt("userId"));
                    feedback.setPid(rs.getInt("pid"));
                    feedback.setComment(rs.getString("comment"));
                    feedback.setRate(rs.getInt("rate"));
                    Timestamp createdAtTimestamp = rs.getTimestamp("createdAt");
                    feedback.setCreatedAt(new Date(createdAtTimestamp.getTime()));
                    feedback.setUserName(rs.getString("fullName"));

                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feedbacks;
    }

    public String getAverageRatingByProductId(int pid) {
        String sql = "SELECT AVG(CAST(rate AS FLOAT)) AS averageRating FROM Feedback WHERE pid = ?";
        double averageRating = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    averageRating = rs.getDouble("averageRating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Format averageRating to show one decimal place
        String formattedAverageRating = String.format("%.1f", averageRating);

        return formattedAverageRating;
    }

    private static final String SELECT_STAR_COUNT_BY_PID = "SELECT COUNT(*) AS starCount FROM Feedback WHERE pid = ? AND rate = ?";

    // Method to calculate total star count by Pid and rating
    public String getTotalStarCountByPid(int pid, int rate) {
        int totalStarCount = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STAR_COUNT_BY_PID)) {

            preparedStatement.setInt(1, pid);
            preparedStatement.setInt(2, rate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    totalStarCount = resultSet.getInt("starCount");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return String.valueOf(totalStarCount);
    }

    public boolean addFeedback(int userId, int pid, String comment, int rate, Date createdAt) {
        String sql = "INSERT INTO Feedback (userId, pid, comment, rate, createdAt) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setInt(2, pid);
            st.setString(3, comment);
            st.setInt(4, rate);
            st.setDate(5, (java.sql.Date) createdAt);
            int rowsInserted = st.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public static void main(String[] args) {
        FeedbackDao feedbackDao = new FeedbackDao();
        int productId = 1; // Example product ID
        System.out.println(feedbackDao.getTotalStarCountByPid(1, 1));

    }

}
