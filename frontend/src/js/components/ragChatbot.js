define(['knockout', '../services/apiService', '../utils/constants', '../utils/errorMessages'], function (ko, apiService, constants, errorMessages) {
    'use strict';
    function escapeHtml(value) { return String(value || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }
    function inlineMarkdown(value) { return value.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>').replace(/__(.+?)__/g, '<strong>$1</strong>'); }
    function renderMarkdown(value) {
        const lines = escapeHtml(value).split(/\r?\n/), html = []; let list = null;
        const close = function () { if (list) { html.push('</' + list + '>'); list = null; } };
        lines.forEach(function (line) {
            const bullet = line.match(/^\s*[-*+]\s+(.+)$/), numbered = line.match(/^\s*\d+[.)]\s+(.+)$/), heading = line.match(/^\s*#{1,3}\s+(.+)$/);
            if (bullet || numbered) { const type = bullet ? 'ul' : 'ol'; if (list !== type) { close(); html.push('<' + type + '>'); list = type; } html.push('<li>' + inlineMarkdown((bullet || numbered)[1]) + '</li>'); }
            else { close(); if (heading) { html.push('<h4>' + inlineMarkdown(heading[1]) + '</h4>'); } else if (line.trim()) { html.push('<p>' + inlineMarkdown(line) + '</p>'); } }
        });
        close(); return html.join('');
    }
    function create() {
        const chat = { isChatOpen: ko.observable(false), isChatSending: ko.observable(false), chatInput: ko.observable(''), chatMessages: ko.observableArray([]) };
        chat.openChat = function () { chat.isChatOpen(true); };
        chat.closeChat = function () { chat.isChatOpen(false); };
        chat.renderAssistantMessage = function (message) { return renderMarkdown(message.text); };
        chat.scrollChatToLatest = function () { window.setTimeout(function () { const area = document.querySelector('.rag-chat-drawer-open .rag-chat-messages'); if (area) { area.scrollTop = area.scrollHeight; } }, 0); };
        chat.sendChat = async function () {
            console.log('ACTUAL CHAT SEND HANDLER EXECUTED');
            const question = chat.chatInput(); if (chat.isChatSending() || !question || !question.trim()) { return; }
            let thinking;
            try {
                thinking = { role: 'assistant', text: 'Thinking...', thinking: true, disclaimer: '' };
                chat.chatMessages.push({ role: 'user', text: question, thinking: false, disclaimer: '' }); chat.chatInput(''); chat.isChatSending(true); chat.chatMessages.push(thinking); chat.scrollChatToLatest();
                console.log('RAG send started', question);
                console.log('RAG endpoint', constants.ENDPOINTS.RAG.CHAT);
                const request = apiService.post(
                    constants.ENDPOINTS.RAG.CHAT,
                    { question: question });
                console.log('RAG API invocation returned', request);
                const response = await request;
                console.log('RAG response received', response);
                if (!response || typeof response.answer !== 'string' || !response.answer.trim()) { throw new Error('Invalid RAG chat response'); }
                chat.chatMessages.remove(thinking); chat.chatMessages.push({ role: 'assistant', text: response.answer, thinking: false, disclaimer: typeof response.disclaimer === 'string' ? response.disclaimer : '' });
                console.log('RAG assistant message appended');
            } catch (error) {
                console.error('RAG chat request failed', error);
                if (thinking) { chat.chatMessages.remove(thinking); }
                chat.chatMessages.push({ role: 'assistant', text: errorMessages.forRequest(error, "Sorry, I couldn't get a response right now. Please try again."), thinking: false, disclaimer: '' });
            } finally { chat.isChatSending(false); console.log('RAG sending state reset'); chat.scrollChatToLatest(); }
        };
        chat.sendChatOnEnter = function (_, event) { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); chat.sendChat(); } return true; };
        return chat;
    }
    return { create: create };
});
