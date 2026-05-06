/**
 * ChatWidget - Shared floating chat panel for both subscribers and professionals.
 * Usage:
 *   - Subscriber: new ChatWidget({ contactId: myNutritionistId, contactName: 'Dr. X', isProfessional: false })
 *   - Professional: new ChatWidget({ contactId: clientId, contactName: 'Client Name', isProfessional: true })
 * Requires main.js (API_BASE, apiRequest, getToken) to be loaded.
 * @author COMP2850 Team
 */

class ChatWidget {
    constructor(options) {
        this.contactId = options.contactId;
        this.contactName = options.contactName || 'Chat';
        this.isProfessional = options.isProfessional || false;
        this.isOpen = false;
        this.pollInterval = null;
        this.pollMs = options.pollMs || 5000;

        if (!this.contactId) {
            this._renderNoContact();
            return;
        }

        this._createElements();
        this._startPolling();
        this._loadUnreadCount();
    }

    _createElements() {
        this.button = document.createElement('button');
        this.button.id = 'chatWidgetBtn';
        this.button.innerHTML = '<i class="fas fa-comments"></i><span id="chatBadge" class="chat-badge" style="display:none">0</span>';
        this.button.addEventListener('click', () => this.toggle());

        this.panel = document.createElement('div');
        this.panel.id = 'chatWidgetPanel';
        this.panel.className = 'chat-widget-panel';
        this.panel.innerHTML = `
            <div class="chat-header">
                <span class="chat-header-name"><i class="fas fa-comments"></i> Chat with ${escapeHtml(this.contactName)}</span>
                <button class="chat-close" id="chatWidgetClose"><i class="fas fa-times"></i></button>
            </div>
            <div class="chat-messages" id="chatMessages"></div>
            <div class="chat-input-bar">
                <input type="text" id="chatInput" class="chat-input" placeholder="Type a message..." autocomplete="off">
                <button class="chat-send-btn" id="chatSendBtn"><i class="fas fa-paper-plane"></i></button>
            </div>
        `;

        document.body.appendChild(this.button);
        document.body.appendChild(this.panel);

        document.getElementById('chatWidgetClose').addEventListener('click', () => this.close());
        document.getElementById('chatSendBtn').addEventListener('click', () => this.send());
        document.getElementById('chatInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.send();
        });
    }

    _renderNoContact() {
        const btn = document.createElement('button');
        btn.id = 'chatWidgetBtn';
        btn.innerHTML = '<i class="fas fa-comments"></i>';
        btn.disabled = true;
        btn.title = 'No nutritionist assigned';
        document.body.appendChild(btn);
    }

    toggle() {
        this.isOpen ? this.close() : this.open();
    }

    async open() {
        this.isOpen = true;
        this.panel.style.display = 'flex';
        this.button.classList.add('chat-active');
        await this._loadConversation();
        this._markAsRead();
        this._hideBadge();
        this._scrollToBottom();
        document.getElementById('chatInput').focus();
    }

    close() {
        this.isOpen = false;
        this.panel.style.display = 'none';
        this.button.classList.remove('chat-active');
    }

    async send() {
        const input = document.getElementById('chatInput');
        const content = input.value.trim();
        if (!content) return;

        input.value = '';
        const btn = document.getElementById('chatSendBtn');
        btn.disabled = true;

        try {
            await apiRequest(`${API_BASE}/messages`, {
                method: 'POST',
                body: JSON.stringify({ receiverId: this.contactId, content })
            });
            await this._loadConversation();
            this._scrollToBottom();
        } catch (e) {
            console.error('Failed to send message:', e);
        } finally {
            btn.disabled = false;
            input.focus();
        }
    }

    async _loadConversation() {
        const container = document.getElementById('chatMessages');
        if (!container) return;

        try {
            const resp = await apiRequest(`${API_BASE}/messages/conversation?otherId=${this.contactId}`);
            if (!resp.ok) return;
            const messages = await resp.json();
            this._renderMessages(messages, container);
        } catch (e) {
            console.error('Failed to load conversation:', e);
        }
    }

    _renderMessages(messages, container) {
        const currentUserId = JSON.parse(localStorage.getItem('user'))?.id;

        if (messages.length === 0) {
            container.innerHTML = '<div class="chat-empty"><i class="fas fa-comment-dots"></i><p>No messages yet. Say hello!</p></div>';
            return;
        }

        let html = '';
        let lastDate = null;

        messages.forEach(msg => {
            const msgDate = msg.createdAt.split('T')[0];
            if (msgDate !== lastDate) {
                html += `<div class="chat-date-separator">${msgDate}</div>`;
                lastDate = msgDate;
            }

            const isMine = msg.senderId === currentUserId;
            const time = new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            html += `<div class="chat-message ${isMine ? 'chat-mine' : 'chat-theirs'}">
                <div class="chat-msg-content">
                    ${!isMine ? `<div class="chat-msg-name">${escapeHtml(msg.senderName)}</div>` : ''}
                    <div class="chat-msg-bubble">${escapeHtml(msg.content)}</div>
                    <div class="chat-msg-time">${time}</div>
                </div>
            </div>`;
        });

        container.innerHTML = html;
    }

    _scrollToBottom() {
        const container = document.getElementById('chatMessages');
        if (container) container.scrollTop = container.scrollHeight;
    }

    async _loadUnreadCount() {
        try {
            const resp = await apiRequest(`${API_BASE}/messages/unread?otherId=${this.contactId}`);
            if (!resp.ok) return;
            const data = await resp.json();
            this._showBadge(data.count);
        } catch (e) {
            console.error('Failed to load unread count:', e);
        }
    }

    async _markAsRead() {
        try {
            await apiRequest(`${API_BASE}/messages/mark-read?otherId=${this.contactId}`, { method: 'POST' });
        } catch (e) {
            console.error('Failed to mark as read:', e);
        }
    }

    _showBadge(count) {
        const badge = document.getElementById('chatBadge');
        if (!badge) return;
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }

    _hideBadge() {
        const badge = document.getElementById('chatBadge');
        if (badge) badge.style.display = 'none';
    }

    _startPolling() {
        this.pollInterval = setInterval(async () => {
            await this._loadUnreadCount();
            if (this.isOpen) {
                await this._loadConversation();
                this._scrollToBottom();
                this._markAsRead();
            }
        }, this.pollMs);
    }

    destroy() {
        if (this.pollInterval) clearInterval(this.pollInterval);
        this.button?.remove();
        this.panel?.remove();
    }
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
