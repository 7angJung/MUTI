// MUTI Main Page JavaScript
// API_BASE_URL is loaded from config.js

// Start button event listener
document.getElementById('startBtn').addEventListener('click', () => {
    // Survey 페이지로 이동
    window.location.href = 'survey.html';
});

// Page load animation
document.addEventListener('DOMContentLoaded', () => {
    console.log('MUTI 애플리케이션 시작됨');

    // API 연결 테스트 (선택적)
    testApiConnection();
});

// API 연결 테스트 함수
async function testApiConnection() {
    try {
        const response = await fetch(`${API_BASE_URL}/surveys/ping`);
        const data = await response.json();

        if (data.success && data.data === 'pong') {
            console.log('✅ API 연결 성공');
        } else {
            console.warn('⚠️ API 응답이 예상과 다릅니다:', data);
        }
    } catch (error) {
        console.error('❌ API 연결 실패:', error);
        console.log('백엔드 서버가 실행 중인지 확인해주세요 (http://localhost:8080)');
    }
}