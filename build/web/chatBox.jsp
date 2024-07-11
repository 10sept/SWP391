<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Chatbox</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
        <style>
            /* Chatbox elements styles */
            #chatbox-wrapper #chatbox-btn-wrapper {
                margin-right: 20px;
                width: 50px;
                height: 50px;
                border-radius: 50% 50%;
                border: 2px solid #B40101;
                background: #DB0630;
                display: flex;
                align-items: center;
                justify-content: center;
                position: fixed;
                right: 1em;
                bottom: 2em;
                animation: IconAnimation 5s ease-in-out infinite;
                cursor: pointer;
                z-index: 99;
            }

            #chatbox-wrapper #chatbox-btn-wrapper span {
                color: #fff;
            }

            #chatbox-wrapper #chatbox-btn-wrapper:hover {
                animation: unset !important;
                transform: scale(1.2);
            }

            @keyframes IconAnimation {
                0% {
                    rotate: 0deg;
                    transform: scale(1);
                }
                25% {
                    rotate: 360deg;
                    transform: scale(1.2);
                }
                50%, 100% {
                    rotate: 720deg;
                    transform: scale(1);
                }
            }

            #chatbox-wrapper #chatbox {
                --chatbox-height: 480px;
                --chatbox-width: 350px;
                position: fixed;
                width: 0;
                height: 0;
                z-index: 100;
                right: 1em;
                bottom: 2em;
                overflow: hidden;
            }

            @media (max-width: 480px) {
                #chatbox-wrapper #chatbox {
                    --chatbox-height: 100%;
                    --chatbox-width: 100%;
                    top: 0;
                    left: 0;
                    bottom: unset;
                    right: unset;
                }
            }

            #chatbox-wrapper #chatbox.show {
                width: var(--chatbox-width);
                height: var(--chatbox-height);
            }

            #chatbox-wrapper #chatbox.closing {
                width: var(--chatbox-width);
                height: var(--chatbox-height);
            }

            #chatbox-wrapper #chatbox .chatbox-dialog {
                width: 100%;
                height: 100%;
                display: flex;
                flex-direction: column;
                transform: translateY(100%);
                transition: transform ease-in-out .5s;
            }

            #chatbox-wrapper #chatbox.show .chatbox-dialog {
                transform: translateY(0%);
            }

            #chatbox-wrapper #chatbox.closing .chatbox-dialog {
                transform: translateY(100%);
            }

            #chatbox-wrapper #chatbox .chatbox-header {
                flex-shrink: 1;
                padding: .5em .75em;
                display: flex;
                justify-content: space-between;
                align-items: center;
                background: #D10024;
                color: #fff;
                font-size: 1.2rem; /* Increased font size */
            }

            #chatbox-wrapper #chatbox .chatbox-close {
                background: transparent;
                outline: unset;
                border: none;
                padding: 5px;
                line-height: 10px;
            }

            #chatbox-wrapper #chatbox .chatbox-close span {
                font-size: 1.2rem; /* Increased font size */
                font-weight: 600;
                color: #fff;
            }

            #chatbox-wrapper #chatbox .chatbox-body {
                flex-grow: 1;
                padding: .5em .75em;
                height: calc(100%);
                width: 100%;
                overflow-x: hidden;
                overflow-y: auto;
                display: flex;
                flex-direction: column-reverse;
                justify-content: end;
                align-items: center;
                background: #fff;
                color: #fff;
            }

            #chatbox-wrapper #chatbox .chatbox-item {
                width: 100%;
                display: flex;
                align-items: start;
                justify-content: start;
                color: #000;
            }

            #chatbox-wrapper #chatbox .chatbox-item .chatbox-user-avatar {
                width: 30px;
                height: 30px;
                border: 1px solid #fff;
                border-radius: 50% 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: #fff;
                box-shadow: 1px 1px 3px #00000075;
                margin-top: .5em;
            }

            #chatbox-wrapper #chatbox .chatbox-item .chatbox-user-avatar span {
                font-size: 1.2rem; /* Increased font size */
                font-weight: 500;
                color: #D10024;
            }

            #chatbox-wrapper #chatbox .chatbox-item .chatbox-item-content-wrapper {
                width: 80%;
                padding: .35em;
                position: relative;
                display: flex;
                flex-direction: column;
            }

            #chatbox-wrapper #chatbox .chatbox-item .chatbox-item-content {
                flex-shrink: 1;
                max-width: 100%;
                color: #363636;
                background: #efeeee;
                font-size: 1.2rem; /* Increased font size */
                padding: .35em;
                border-radius: .35em;
                align-self: flex-start;
            }

            #chatbox-wrapper #chatbox .chatbox-item.chatbox-msg-receiver {
                flex-direction: row-reverse;
                justify-content: end;
            }

            #chatbox-wrapper #chatbox .chatbox-item.chatbox-msg-receiver .chatbox-item-content {
                align-self: flex-end;
                background: #cecece;
                color: #363636;
            }

            #chatbox-wrapper #chatbox .chatbox-item.chatbox-msg-receiver .chatbox-user-avatar {
                border-color: #cecece;
                background: #fff;
            }

            #chatbox-wrapper #chatbox .chatbox-item.chatbox-msg-receiver .chatbox-user-avatar span {

                color: #DB0630;
            }

            #chatbox-wrapper #chatbox .chatbox-footer {
                flex-shrink: 1;
                padding: .35em;
                background-color: #efeeee;
                display: flex;
                width: 100%;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-field-wrapper {
                width: 85%;
                line-height: 12px;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-field-wrapper textarea {
                --chatbox-max-height: 55;
                resize: none;
                height: 32px;
                padding: 0.65em 0.35em;
                font-size: 1.2rem; /* Increased font size */
                width: 100%;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-field-wrapper textarea:focus {
                border-color: #2ea8ee;
                outline-color: #2ea8ee;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-btn-wrapper {
                width: 15%;
                overflow: hidden;
                padding: 0 .35em;
                display: flex;
                justify-content: center;
                align-items: center;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-btn-wrapper #chatbox-send-btn {
                flex-grow: 1;
                width: 100%;
                height: 100%;
                background-color: #D10024;
                padding: .35em;
                line-height: 12px;
                border: none;
            }

            #chatbox-wrapper #chatbox .chatbox-footer .chatbox-btn-wrapper #chatbox-send-btn span {
                font-size: 1.2rem; /* Increased font size */
                color: #fff;
            }
            #chatbox-wrapper #chatbox .chatbox-item .chatbox-timestamp {
                font-size: 0.8rem;
                color: #666;
                margin-top: 0.25em;
                text-align: right;
                width: 100%;
            }
        </style>
    </head>
    <body>
        <!-- Chatbox Wrapper -->

        <div id="chatbox-wrapper">
            <div id="chatbox-btn-wrapper">
                <span class="material-symbols-outlined">chat</span>
            </div>
            <div id="chatbox">
                <div class="chatbox-dialog">
                    <div class="chatbox-header">
                        <div class="chat-title">Site Chatbox</div>
                        <div class="chat-tools">
                            <button class="chatbox-close"><span class="material-symbols-outlined">close</span></button>
                        </div>
                    </div>
                    <div class="chatbox-body">
                        <!-- Sample messages with timestamps -->
                        <div class="chatbox-item chatbox-msg-receiver">
                            <div class="chatbox-user-avatar"><span class="material-symbols-outlined">person</span></div>
                            <div class="chatbox-item-content-wrapper">
                                <span class="chatbox-item-content">Donec ac augue sed mauris bibendum scelerisque.</span>
                                <div class="chatbox-timestamp">12:30 PM</div>
                            </div>
                        </div>
                        <div class="chatbox-item chatbox-msg-sender">
                            <div class="chatbox-user-avatar"><span class="material-symbols-outlined">person</span></div>
                            <div class="chatbox-item-content-wrapper">
                                <span class="chatbox-item-content">Integer ultrices ante massa, vitae bibendum enim tempus et</span>
                                <div class="chatbox-timestamp">12:31 PM</div>
                            </div>
                        </div>
                    </div>
                    <div class="chatbox-footer">
                        <div class="chatbox-field-wrapper">
                            <textarea name="message" id="chatbox-message-field" placeholder="Type a message..."></textarea>
                        </div>
                        <div class="chatbox-btn-wrapper">
                            <button id="chatbox-send-btn"><span class="material-symbols-outlined">send</span></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Chatbox Wrapper -->
        <script>
            const chatBoxIcon = document.getElementById('chatbox-btn-wrapper');
            const chatBoxCloseBtn = document.querySelector('#chatbox .chatbox-close');
            const chatBoxWrapper = document.getElementById('chatbox');
            const chatBoxTextField = document.getElementById('chatbox-message-field');
            const chatBoxBody = document.querySelector('.chatbox-body');

            chatBoxIcon.addEventListener('click', e => {
                e.preventDefault();
                if (chatBoxWrapper.classList.contains('show')) {
                    chatBoxWrapper.classList.remove('show');
                } else {
                    chatBoxWrapper.classList.add('show');
                    chatBoxIcon.style.display = 'none';
                }
            });

            chatBoxCloseBtn.addEventListener('click', e => {
                e.preventDefault();
                if (chatBoxWrapper.classList.contains('show')) {
                    if (!chatBoxWrapper.classList.contains('closing'))
                        chatBoxWrapper.classList.add('closing');
                    setTimeout(() => {
                        chatBoxWrapper.classList.remove('show');
                        chatBoxWrapper.classList.remove('closing');
                    }, 500);
                }
                chatBoxIcon.removeAttribute('style');
            });

            const chatBoxTextFieldHeight = chatBoxTextField.clientHeight;
            chatBoxTextField.addEventListener('keyup', e => {
                var maxHeight = getComputedStyle(chatBoxTextField).getPropertyValue('--chatbox-max-height');
                chatBoxTextField.removeAttribute('style');
                setTimeout(() => {
                    if (chatBoxTextField.scrollHeight > maxHeight) {
                        chatBoxTextField.style.height = `${maxHeight}px`;
                    } else {
                        chatBoxTextField.style.height = `${chatBoxTextField.scrollHeight}px`;
                    }
                }, 0);
            });

            const chatBoxSendBtn = document.querySelector("#chatbox-send-btn");
            chatBoxSendBtn.addEventListener("click", () => {
                const messageContent = chatBoxTextField.value.trim();
                if (messageContent) {
                    const currentTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
                    const newMessage = document.createElement("div");
                    newMessage.classList.add("chatbox-item", "chatbox-msg-sender");
                    newMessage.innerHTML = `
                         <div class="chatbox-user-avatar">
                             <span class="material-symbols-outlined">person</span>
                         </div>
                         <div class="chatbox-item-content-wrapper">
                             <span class="chatbox-item-content">${messageContent}</span>
                             <div class="chatbox-timestamp">${currentTime}</div>
                         </div>
                     `;
                    chatBoxBody.prepend(newMessage);
                    chatBoxTextField.value = "";
                }
            });
        </script>
    </body>
</html>
