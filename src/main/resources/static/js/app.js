/* ================================================
   Chat0z — Frontend Application Logic
   ================================================ */

'use strict';

const App = (function () {

  // ── State ──────────────────────────────────────────────────
  let currentUser   = null;   // UserPrivateResponseDTO
  let rooms         = [];     // RoomResponseDTO[]
  let currentRoom   = null;   // RoomResponseDTO
  let stompClient   = null;
  let subscription  = null;
  let roomInvites   = {};     // roomId → inviteCode (cached for delete)

  // ── DOM refs ───────────────────────────────────────────────
  const loginView      = document.getElementById('login-view');
  const appView        = document.getElementById('app-view');
  const sidebar        = document.getElementById('sidebar');
  const userAvatar     = document.getElementById('user-avatar');
  const userNickname   = document.getElementById('user-nickname');
  const roomsList      = document.getElementById('rooms-list');
  const roomsCount     = document.getElementById('rooms-count');
  const chatRoomName   = document.getElementById('chat-room-name');
  const roomIdBadge    = document.getElementById('chat-room-id');
  const welcomeScreen  = document.getElementById('welcome-screen');
  const messagesArea   = document.getElementById('messages-area');
  const messagesList   = document.getElementById('messages-list');
  const chatFooter     = document.getElementById('chat-footer');
  const messageInput   = document.getElementById('message-input');
  const btnRoomInvite  = document.getElementById('btn-room-invite');
  const btnCreateRoom  = document.getElementById('btn-create-room');
  const btnJoinRoom    = document.getElementById('btn-join-room');
  const btnSend        = document.getElementById('btn-send');
  const modalCreate    = document.getElementById('modal-create');
  const modalJoin      = document.getElementById('modal-join');
  const inputRoomName  = document.getElementById('input-room-name');
  const inputInvCode   = document.getElementById('input-invite-code');
  const inputJoinCode  = document.getElementById('input-join-code');
  const createError    = document.getElementById('create-room-error');
  const joinError      = document.getElementById('join-room-error');
  const btnConfirmCreate = document.getElementById('btn-confirm-create');
  const btnConfirmJoin   = document.getElementById('btn-confirm-join');
  const toastContainer   = document.getElementById('toast-container');

  // ── API helpers ────────────────────────────────────────────
  async function api(path, options = {}) {
    const res = await fetch(path, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...options.headers },
      ...options,
    });
    if (res.status === 204) return null;
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw Object.assign(new Error(text || res.statusText), { status: res.status });
    }
    return res.json();
  }

  // ── Toast notifications ────────────────────────────────────
  function toast(message, type = 'info', durationMs = 3500) {
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = message;
    toastContainer.appendChild(el);
    setTimeout(() => {
      el.style.animation = 'slideOut .2s ease forwards';
      el.addEventListener('animationend', () => el.remove());
    }, durationMs);
  }

  // ── Auth check ─────────────────────────────────────────────
  async function init() {
    try {
      currentUser = await api('/user/me');
      showApp();
      await loadRooms();
    } catch (err) {
      showLogin();
    }
  }

  function showLogin() {
    loginView.classList.remove('hidden');
    appView.classList.add('hidden');
  }

  async function showApp() {
    loginView.classList.add('hidden');
    appView.classList.remove('hidden');

    userNickname.textContent = currentUser.nickname || currentUser.email || 'Usuário';
    if (currentUser.picProfile) userAvatar.src = currentUser.picProfile;
  }

  // ── Rooms ──────────────────────────────────────────────────
  async function loadRooms() {
    try {
      rooms = await api('/room/me');
      renderRooms();
    } catch {
      toast('Erro ao carregar salas', 'error');
    }
  }

  function renderRooms() {
    roomsCount.textContent = rooms.length;
    if (rooms.length === 0) {
      roomsList.innerHTML = '<li class="room-empty">Nenhuma sala ainda</li>';
      return;
    }
    roomsList.innerHTML = rooms.map(r => `
      <li class="room-item ${currentRoom && currentRoom.roomId === r.roomId ? 'active' : ''}"
          data-room-id="${r.roomId}"
          data-room-name="${escapeHtml(r.name)}"
          title="${escapeHtml(r.name)}">
        <span class="room-icon">#</span>
        <span class="room-name">${escapeHtml(r.name)}</span>
      </li>
    `).join('');

    roomsList.querySelectorAll('.room-item').forEach(li => {
      li.addEventListener('click', () => selectRoom({
        roomId: Number(li.dataset.roomId),
        name: li.dataset.roomName,
      }));
    });
  }

  async function selectRoom(room) {
    if (currentRoom && currentRoom.roomId === room.roomId) return;

    disconnectWebSocket();
    currentRoom = room;
    renderRooms();

    chatRoomName.textContent = room.name;
    roomIdBadge.textContent = `#${room.roomId}`;
    roomIdBadge.classList.remove('hidden');
    btnRoomInvite.classList.remove('hidden');

    welcomeScreen.classList.add('hidden');
    messagesArea.classList.remove('hidden');
    chatFooter.classList.remove('hidden');

    messagesList.innerHTML = '';

    await loadMessages(room.roomId);
    connectWebSocket(room.roomId);

    // Mobile: close sidebar after selecting room
    sidebar.classList.remove('open');
  }

  // ── Messages ───────────────────────────────────────────────
  async function loadMessages(roomId) {
    try {
      const messages = await api(`/messages/${roomId}`);
      messagesList.innerHTML = '';
      messages.forEach(m => appendMessage(m));
      scrollToBottom();
    } catch {
      toast('Erro ao carregar mensagens', 'error');
    }
  }

  function appendMessage(msg) {
    const isOwn = msg.senderName === (currentUser.nickname || currentUser.email.split('@')[0]);
    const li = document.createElement('li');
    li.className = `msg-item ${isOwn ? 'own' : 'other'}`;
    li.dataset.msgId = msg.id;

    const time = formatTime(msg.sendAt);
    li.innerHTML = `
      <div class="msg-meta">
        ${!isOwn ? `<span class="msg-sender">${escapeHtml(msg.senderName)}</span>` : ''}
        <span class="msg-time">${time}</span>
      </div>
      <div class="msg-bubble">${escapeHtml(msg.content)}</div>
      <div class="msg-actions">
        <button class="btn-delete-msg" data-msg-id="${msg.id}" title="Deletar mensagem">🗑 Deletar</button>
      </div>
    `;

    li.querySelector('.btn-delete-msg').addEventListener('click', (e) => {
      e.stopPropagation();
      deleteMessage(Number(li.dataset.msgId));
    });

    messagesList.appendChild(li);
  }

  function removeMessage(messageId) {
    const li = messagesList.querySelector(`[data-msg-id="${messageId}"]`);
    if (li) {
      li.style.opacity = '0';
      li.style.transition = 'opacity .2s';
      setTimeout(() => li.remove(), 200);
    }
  }

  function scrollToBottom() {
    messagesArea.scrollTop = messagesArea.scrollHeight;
  }

  // ── WebSocket ──────────────────────────────────────────────
  function connectWebSocket(roomId) {
    const socket = new SockJS('/connect');
    stompClient = new StompJs.Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        subscription = stompClient.subscribe(`/topic/room/${roomId}`, (frame) => {
          try {
            const data = JSON.parse(frame.body);
            if (data.deleteMessageId !== undefined) {
              removeMessage(data.deleteMessageId);
            } else {
              appendMessage(data);
              scrollToBottom();
            }
          } catch (e) {
            console.error('Failed to parse WebSocket message', e);
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
        toast('Conexão perdida. Reconectando...', 'error');
      },
    });
    stompClient.activate();
  }

  function disconnectWebSocket() {
    if (subscription) { subscription.unsubscribe(); subscription = null; }
    if (stompClient)  { stompClient.deactivate(); stompClient = null; }
  }

  // ── Send / Delete ──────────────────────────────────────────
  function sendMessage() {
    const content = messageInput.value.trim();
    if (!content || !currentRoom || !stompClient || !stompClient.connected) return;

    stompClient.publish({
      destination: `/app/chat/${currentRoom.roomId}`,
      body: JSON.stringify({ userId: currentUser.userId, content }),
    });

    messageInput.value = '';
    messageInput.style.height = 'auto';
  }

  function deleteMessage(messageId) {
    if (!currentRoom || !stompClient || !stompClient.connected) return;
    stompClient.publish({
      destination: `/app/chat/${currentRoom.roomId}/delete`,
      body: JSON.stringify({ messageId, userId: currentUser.userId }),
    });
  }

  // ── Create Room ────────────────────────────────────────────
  async function createRoom() {
    const name   = inputRoomName.value.trim();
    const invite = inputInvCode.value.trim();

    createError.classList.add('hidden');

    if (!name || !invite) {
      createError.textContent = 'Preencha todos os campos.';
      createError.classList.remove('hidden');
      return;
    }
    if (invite.length < 4) {
      createError.textContent = 'O código de convite deve ter pelo menos 4 caracteres.';
      createError.classList.remove('hidden');
      return;
    }

    btnConfirmCreate.disabled = true;
    try {
      const room = await api('/room/create', {
        method: 'POST',
        body: JSON.stringify({ name, invite }),
      });
      const roomWithInvite = { ...room, invite };
      closeModal(modalCreate);
      toast(`Sala "${room.name}" criada!`, 'success');
      rooms.push(roomWithInvite);
      renderRooms();
      await selectRoom(roomWithInvite);
    } catch (err) {
      createError.textContent = err.message || 'Erro ao criar sala. Tente outro código de convite.';
      createError.classList.remove('hidden');
    } finally {
      btnConfirmCreate.disabled = false;
    }
  }

  // ── Join Room ──────────────────────────────────────────────
  async function joinRoom() {
    const inviteCode = inputJoinCode.value.trim();
    joinError.classList.add('hidden');

    if (!inviteCode) {
      joinError.textContent = 'Informe o código de convite.';
      joinError.classList.remove('hidden');
      return;
    }

    btnConfirmJoin.disabled = true;
    try {
      const room = await api('/room/enter', {
        method: 'POST',
        body: JSON.stringify({ inviteCode }),
      });
      const roomWithInvite = { ...room, invite: inviteCode };
      closeModal(modalJoin);
      toast(`Você entrou em "${room.name}"!`, 'success');
      if (!rooms.find(r => r.roomId === room.roomId)) rooms.push(roomWithInvite);
      renderRooms();
      await selectRoom(roomWithInvite);
    } catch (err) {
      joinError.textContent = err.message || 'Código de convite inválido ou você já é membro.';
      joinError.classList.remove('hidden');
    } finally {
      btnConfirmJoin.disabled = false;
    }
  }

  // ── Modals ─────────────────────────────────────────────────
  function openModal(modal) {
    modal.classList.remove('hidden');
    const firstInput = modal.querySelector('input');
    if (firstInput) setTimeout(() => firstInput.focus(), 50);
  }

  function closeModal(modal) {
    modal.classList.add('hidden');
    modal.querySelectorAll('input').forEach(i => { i.value = ''; });
    modal.querySelectorAll('.form-error').forEach(e => e.classList.add('hidden'));
  }

  // ── Helpers ────────────────────────────────────────────────
  function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function formatTime(isoStr) {
    if (!isoStr) return '';
    try {
      // LocalDateTime.toString() → "2024-01-15T14:30:45.123456789"
      const d = new Date(isoStr.length === 19 ? isoStr + 'Z' : isoStr);
      if (isNaN(d)) return isoStr;
      return d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoStr;
    }
  }

  // ── Event listeners ────────────────────────────────────────
  function bindEvents() {
    // Send message
    btnSend.addEventListener('click', sendMessage);
    messageInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
      }
    });

    // Auto-resize textarea
    messageInput.addEventListener('input', () => {
      messageInput.style.height = 'auto';
      messageInput.style.height = Math.min(messageInput.scrollHeight, 120) + 'px';
    });

    // Modals — open
    btnCreateRoom.addEventListener('click', () => openModal(modalCreate));
    btnJoinRoom.addEventListener('click',   () => openModal(modalJoin));

    // Modals — close buttons
    document.querySelectorAll('.modal-close, [data-modal]').forEach(btn => {
      btn.addEventListener('click', () => {
        const modalId = btn.dataset.modal || btn.closest('.modal-overlay').id;
        closeModal(document.getElementById(modalId));
      });
    });

    // Modals — close on overlay click
    [modalCreate, modalJoin].forEach(m => {
      m.addEventListener('click', (e) => { if (e.target === m) closeModal(m); });
    });

    // Modals — confirm
    btnConfirmCreate.addEventListener('click', createRoom);
    btnConfirmJoin.addEventListener('click',   joinRoom);

    // Modals — enter key
    [inputRoomName, inputInvCode].forEach(inp => {
      inp.addEventListener('keydown', (e) => { if (e.key === 'Enter') createRoom(); });
    });
    inputJoinCode.addEventListener('keydown', (e) => { if (e.key === 'Enter') joinRoom(); });

    // Copy invite code
    btnRoomInvite.addEventListener('click', async () => {
      if (!currentRoom) return;
      const code = currentRoom.invite;
      if (!code) {
        toast('Código de convite não disponível. Reabra a sala.', 'info');
        return;
      }
      try {
        await navigator.clipboard.writeText(code);
        toast('Código de convite copiado!', 'info');
      } catch {
        toast('Não foi possível copiar.', 'error');
      }
    });

    // Mobile sidebar toggle
    document.getElementById('btn-open-sidebar').addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });
    document.getElementById('btn-toggle-sidebar').addEventListener('click', () => {
      sidebar.classList.remove('open');
    });

    // Escape key closes modals
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        if (!modalCreate.classList.contains('hidden')) closeModal(modalCreate);
        if (!modalJoin.classList.contains('hidden'))   closeModal(modalJoin);
      }
    });
  }

  // ── Bootstrap ──────────────────────────────────────────────
  function bootstrap() {
    bindEvents();
    init();
  }

  return { bootstrap };
})();

document.addEventListener('DOMContentLoaded', App.bootstrap);
