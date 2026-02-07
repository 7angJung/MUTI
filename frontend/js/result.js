// MUTI Result Page JavaScript

// DOM Elements
const loadingState = document.getElementById('loadingState');
const resultContainer = document.getElementById('resultContainer');
const errorState = document.getElementById('errorState');
const errorMessage = document.getElementById('errorMessage');

const mutiTypeBadge = document.getElementById('mutiTypeBadge');
const mutiTypeText = document.getElementById('mutiTypeText');
const mutiTypeName = document.getElementById('mutiTypeName');
const mutiTypeDescription = document.getElementById('mutiTypeDescription');

const axisEI = document.getElementById('axisEI');
const axisSF = document.getElementById('axisSF');
const axisAD = document.getElementById('axisAD');
const axisPU = document.getElementById('axisPU');

const axisEIValue = document.getElementById('axisEIValue');
const axisSFValue = document.getElementById('axisSFValue');
const axisADValue = document.getElementById('axisADValue');
const axisPUValue = document.getElementById('axisPUValue');

const shareBtn = document.getElementById('shareBtn');

// MUTI Type Descriptions
const MUTI_DESCRIPTIONS = {
    'ESAP': {
        name: '에너제틱 스토리텔러',
        description: '빠르고 에너지 넘치는 음악을 좋아하며, 가사의 메시지와 스토리를 중요하게 여깁니다. 활동적이고 대중적인 음악을 선호하는 당신은 신나는 비트와 의미 있는 가사가 함께하는 음악을 즐깁니다.',
        emoji: '🎤⚡'
    },
    'ESAU': {
        name: '독창적 액티비스트',
        description: '에너지 넘치고 활동적이지만 독특하고 실험적인 음악을 추구합니다. 가사의 메시지를 중요시하며, 남들과 다른 자신만의 음악 취향을 가지고 있습니다.',
        emoji: '🎸✨'
    },
    'ESDP': {
        name: '몽환적 시인',
        description: '빠른 템포의 음악을 좋아하지만 감성적이고 몽환적인 분위기를 선호합니다. 가사의 스토리텔링을 중시하며 대중적인 음악을 즐깁니다.',
        emoji: '🌙📖'
    },
    'ESDU': {
        name: '예술적 탐험가',
        description: '빠른 음악에 몽환적 감성을 더하며, 가사의 깊이를 추구합니다. 독특하고 실험적인 사운드를 좋아하는 예술적 성향이 강합니다.',
        emoji: '🎨🚀'
    },
    'EFAP': {
        name: '감성적 댄서',
        description: '빠르고 활동적인 음악을 좋아하며, 멜로디와 감정 표현을 중시합니다. 대중적이고 트렌디한 음악에서 에너지를 얻습니다.',
        emoji: '💃🎶'
    },
    'EFAU': {
        name: '실험적 크리에이터',
        description: '에너지 넘치고 활동적인 음악에 독특한 감성을 더합니다. 멜로디의 감정 표현을 중시하며 새로운 사운드를 탐구합니다.',
        emoji: '🎹🔮'
    },
    'EFDP': {
        name: '드리미 비트메이커',
        description: '빠른 템포에 몽환적인 멜로디를 선호합니다. 감정적이면서도 대중적인 음악을 좋아하며, 분위기를 중시합니다.',
        emoji: '☁️🎧'
    },
    'EFDU': {
        name: '아방가르드 아티스트',
        description: '빠른 리듬에 몽환적 멜로디, 그리고 독특한 사운드를 추구합니다. 실험적이고 감성적인 음악을 즐기는 예술가 타입입니다.',
        emoji: '🌌🎵'
    },
    'ISAP': {
        name: '성찰적 작사가',
        description: '차분하고 내성적인 음악을 선호하며, 가사의 의미와 메시지를 깊이 생각합니다. 활동적이지만 대중적인 음악에서 위안을 찾습니다.',
        emoji: '📝☕'
    },
    'ISAU': {
        name: '철학적 뮤지션',
        description: '조용하고 성찰적인 음악을 좋아하며, 가사의 깊은 의미를 탐구합니다. 독특하고 개성 있는 음악을 선호합니다.',
        emoji: '🎼💭'
    },
    'ISDP': {
        name: '감성 발라더',
        description: '느리고 차분한 음악에 깊은 가사를 선호합니다. 몽환적이고 감성적인 대중 음악을 즐깁니다.',
        emoji: '🌃🎹'
    },
    'ISDU': {
        name: '내면의 탐구자',
        description: '조용하고 성찰적이며, 독특하고 실험적인 음악을 추구합니다. 가사의 깊이와 몽환적 분위기를 모두 중시합니다.',
        emoji: '🔍🌊'
    },
    'IFAP': {
        name: '감각적 리스너',
        description: '차분한 멜로디와 감정 표현을 중시합니다. 활동적이지만 대중적인 음악에서 감성을 느낍니다.',
        emoji: '🎧💫'
    },
    'IFAU': {
        name: '섬세한 큐레이터',
        description: '내성적이면서 감성적인 음악을 선호하며, 독특하고 개성 있는 멜로디를 찾아 듣습니다.',
        emoji: '🎨🎻'
    },
    'IFDP': {
        name: '몽환적 드리머',
        description: '느리고 차분한 멜로디에 몽환적인 분위기를 더한 음악을 좋아합니다. 대중적이면서도 감성적인 음악을 선호합니다.',
        emoji: '🌙✨'
    },
    'IFDU': {
        name: '신비로운 사색가',
        description: '가장 내성적이고 독특한 타입으로, 몽환적이고 실험적인 멜로디를 즐깁니다. 깊은 감성과 독창성을 추구합니다.',
        emoji: '🌌🔮'
    }
};

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    console.log('결과 페이지 로드됨');
    loadResult();
});

// Load result
function loadResult() {
    try {
        showLoading();

        // Get result from sessionStorage
        const resultData = sessionStorage.getItem('mutiResult');

        if (!resultData) {
            throw new Error('결과 데이터를 찾을 수 없습니다. 설문을 다시 진행해주세요.');
        }

        const result = JSON.parse(resultData);
        console.log('결과 데이터:', result);

        displayResult(result);

    } catch (error) {
        console.error('결과 로드 실패:', error);
        showError(error.message);
    }
}

// Display result
function displayResult(result) {
    const mutiType = result.mutiType;
    const typeInfo = MUTI_DESCRIPTIONS[mutiType] || {
        name: result.mutiTypeName || mutiType,
        description: '당신만의 독특한 음악 취향을 가지고 있습니다.',
        emoji: '🎵'
    };

    // Update MUTI type
    mutiTypeText.textContent = mutiType;
    mutiTypeName.textContent = typeInfo.name;
    mutiTypeDescription.textContent = typeInfo.description;

    // Add emoji to badge
    const emojiSpan = document.createElement('div');
    emojiSpan.textContent = typeInfo.emoji;
    emojiSpan.style.fontSize = '2rem';
    emojiSpan.style.marginTop = '10px';
    mutiTypeBadge.appendChild(emojiSpan);

    // Update axis scores
    const scores = result.axisScores;

    // E_I axis
    updateAxisBar(axisEI, axisEIValue, scores.E_I, 'E', 'I');

    // S_F axis
    updateAxisBar(axisSF, axisSFValue, scores.S_F, 'S', 'F');

    // A_D axis
    updateAxisBar(axisAD, axisADValue, scores.A_D, 'A', 'D');

    // P_U axis
    updateAxisBar(axisPU, axisPUValue, scores.P_U, 'P', 'U');

    showResult();

    // Add animation delay
    setTimeout(() => {
        animateAxisBars();
    }, 300);
}

// Update axis bar
function updateAxisBar(barElement, valueElement, score, leftType, rightType) {
    const absScore = Math.abs(score);
    const maxScore = 10; // Assuming max score is 10
    const percentage = (absScore / maxScore) * 50; // 50% is the center

    // Set value text
    valueElement.textContent = score > 0 ? `+${score}` : score;

    // Set bar width (will be animated later)
    barElement.dataset.width = `${percentage}%`;

    // Set bar direction class
    if (score > 0) {
        barElement.className = `axis-bar-fill axis-${leftType.toLowerCase()}`;
    } else {
        barElement.className = `axis-bar-fill axis-${rightType.toLowerCase()}`;
    }

    // Initially set to 0 for animation
    barElement.style.width = '0%';
}

// Animate axis bars
function animateAxisBars() {
    const bars = [axisEI, axisSF, axisAD, axisPU];

    bars.forEach((bar, index) => {
        setTimeout(() => {
            bar.style.width = bar.dataset.width;
        }, index * 200);
    });
}

// Share button
shareBtn.addEventListener('click', () => {
    const resultData = JSON.parse(sessionStorage.getItem('mutiResult'));
    const mutiType = resultData.mutiType;
    const typeInfo = MUTI_DESCRIPTIONS[mutiType];

    const shareText = `나의 MUTI 타입은 ${mutiType} - ${typeInfo.name}! ${typeInfo.emoji}\n\n당신의 음악 취향은? MUTI 테스트 하러가기 👉`;
    const shareUrl = window.location.origin + window.location.pathname.replace('result.html', 'index.html');

    // Try Web Share API
    if (navigator.share) {
        navigator.share({
            title: 'MUTI 음악 성향 테스트',
            text: shareText,
            url: shareUrl
        }).catch(err => console.log('공유 취소:', err));
    } else {
        // Fallback: Copy to clipboard
        const fullText = `${shareText}\n${shareUrl}`;
        navigator.clipboard.writeText(fullText).then(() => {
            alert('결과가 클립보드에 복사되었습니다! 🎉\n\n친구들과 공유해보세요!');
        }).catch(() => {
            // If clipboard API fails, show text
            prompt('아래 텍스트를 복사해서 공유하세요:', fullText);
        });
    }
});

// UI State Management
function showLoading() {
    loadingState.style.display = 'block';
    resultContainer.style.display = 'none';
    errorState.style.display = 'none';
}

function showResult() {
    loadingState.style.display = 'none';
    resultContainer.style.display = 'block';
    errorState.style.display = 'none';
}

function showError(message) {
    loadingState.style.display = 'none';
    resultContainer.style.display = 'none';
    errorState.style.display = 'block';
    errorMessage.textContent = message;
}

// Clear result on page unload
window.addEventListener('beforeunload', () => {
    // Optionally clear sessionStorage when leaving result page
    // sessionStorage.removeItem('mutiResult');
});