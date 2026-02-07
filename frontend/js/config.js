// MUTI API Configuration
// 배포 환경에 따라 자동으로 API URL을 선택합니다

const API_CONFIG = {
    // 로컬 개발 환경
    development: 'http://localhost:8080/api/v1',

    // 프로덕션 환경 (Railway 배포 후 업데이트 필요)
    production: 'https://muti-production.up.railway.app/api/v1'
};

// 현재 환경 감지
const isLocalhost = window.location.hostname === 'localhost' ||
                    window.location.hostname === '127.0.0.1';

// API Base URL 설정
const API_BASE_URL = isLocalhost ? API_CONFIG.development : API_CONFIG.production;

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_BASE_URL };
}