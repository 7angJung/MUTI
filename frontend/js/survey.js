// MUTI Survey Page JavaScript
// API_BASE_URL is loaded from config.js

// State
let surveyData = null;
let currentQuestionIndex = 0;
let answers = []; // { questionId, optionId }

// DOM Elements
const loadingState = document.getElementById('loadingState');
const surveyContainer = document.getElementById('surveyContainer');
const errorState = document.getElementById('errorState');
const errorMessage = document.getElementById('errorMessage');

const progressBar = document.getElementById('progressBar');
const currentQuestionEl = document.getElementById('currentQuestion');
const totalQuestionsEl = document.getElementById('totalQuestions');
const questionTitle = document.getElementById('questionTitle');
const optionsContainer = document.getElementById('optionsContainer');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    console.log('설문 페이지 로드됨');
    loadSurvey();
});

// Load Survey from API
async function loadSurvey() {
    try {
        showLoading();

        const response = await fetch(`${API_BASE_URL}/surveys/1`);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: 설문을 불러올 수 없습니다`);
        }

        const data = await response.json();

        if (!data.success || !data.data) {
            throw new Error('설문 데이터가 올바르지 않습니다');
        }

        surveyData = data.data;
        console.log('설문 데이터 로드 성공:', surveyData);

        // Initialize answers array
        answers = surveyData.questions.map(q => ({
            questionId: q.id,
            optionId: null
        }));

        showSurvey();
        displayQuestion(0);

    } catch (error) {
        console.error('설문 로드 실패:', error);
        showError(error.message);
    }
}

// Display current question
function displayQuestion(index) {
    const question = surveyData.questions[index];
    const totalQuestions = surveyData.questions.length;

    // Update progress
    currentQuestionIndex = index;
    const progress = ((index + 1) / totalQuestions) * 100;
    progressBar.style.width = `${progress}%`;
    currentQuestionEl.textContent = index + 1;
    totalQuestionsEl.textContent = totalQuestions;

    // Update question title
    questionTitle.textContent = question.content;

    // Clear and render options
    optionsContainer.innerHTML = '';
    question.options.forEach(option => {
        const button = document.createElement('button');
        button.className = 'option-button';
        button.textContent = option.content;
        button.dataset.optionId = option.id;

        // Check if this option was previously selected
        if (answers[index].optionId === option.id) {
            button.classList.add('selected');
        }

        button.addEventListener('click', () => selectOption(option.id));
        optionsContainer.appendChild(button);
    });

    // Update navigation buttons
    prevBtn.style.visibility = index > 0 ? 'visible' : 'hidden';

    // Enable next button if answer is selected
    nextBtn.disabled = answers[index].optionId === null;

    // Change button text for last question
    if (index === totalQuestions - 1) {
        nextBtn.textContent = '결과 보기 ✨';
    } else {
        nextBtn.textContent = '다음 →';
    }

    // Add animation
    questionTitle.style.animation = 'none';
    optionsContainer.style.animation = 'none';
    setTimeout(() => {
        questionTitle.style.animation = 'fadeInUp 0.5s ease';
        optionsContainer.style.animation = 'fadeInUp 0.5s ease 0.1s';
    }, 10);
}

// Select option
function selectOption(optionId) {
    // Update answer
    answers[currentQuestionIndex].optionId = optionId;

    // Update UI
    const buttons = optionsContainer.querySelectorAll('.option-button');
    buttons.forEach(btn => {
        if (parseInt(btn.dataset.optionId) === optionId) {
            btn.classList.add('selected');
        } else {
            btn.classList.remove('selected');
        }
    });

    // Enable next button
    nextBtn.disabled = false;
}

// Navigation buttons
prevBtn.addEventListener('click', () => {
    if (currentQuestionIndex > 0) {
        displayQuestion(currentQuestionIndex - 1);
    }
});

nextBtn.addEventListener('click', () => {
    const totalQuestions = surveyData.questions.length;

    if (currentQuestionIndex < totalQuestions - 1) {
        // Go to next question
        displayQuestion(currentQuestionIndex + 1);
    } else {
        // Submit survey
        submitSurvey();
    }
});

// Submit survey
async function submitSurvey() {
    try {
        console.log('설문 제출 중...', answers);

        // Disable button to prevent double submission
        nextBtn.disabled = true;
        nextBtn.textContent = '제출 중...';

        // Generate unique session ID
        const sessionId = `session-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

        const requestBody = {
            surveyId: surveyData.id,
            answers: answers,
            sessionId: sessionId
        };

        console.log('제출 데이터:', requestBody);

        const response = await fetch(`${API_BASE_URL}/surveys/${surveyData.id}/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '설문 제출에 실패했습니다');
        }

        const data = await response.json();

        if (!data.success || !data.data) {
            throw new Error('응답 데이터가 올바르지 않습니다');
        }

        console.log('설문 제출 성공:', data.data);

        // Save result to sessionStorage
        sessionStorage.setItem('mutiResult', JSON.stringify(data.data));

        // Redirect to result page
        window.location.href = 'result.html';

    } catch (error) {
        console.error('설문 제출 실패:', error);
        alert(`오류가 발생했습니다: ${error.message}\n\n다시 시도해주세요.`);

        // Re-enable button
        nextBtn.disabled = false;
        nextBtn.textContent = '결과 보기 ✨';
    }
}

// UI State Management
function showLoading() {
    loadingState.style.display = 'block';
    surveyContainer.style.display = 'none';
    errorState.style.display = 'none';
}

function showSurvey() {
    loadingState.style.display = 'none';
    surveyContainer.style.display = 'block';
    errorState.style.display = 'none';
}

function showError(message) {
    loadingState.style.display = 'none';
    surveyContainer.style.display = 'none';
    errorState.style.display = 'block';
    errorMessage.textContent = message;
}

// Prevent accidental page leave
window.addEventListener('beforeunload', (e) => {
    // Only show warning if user has started answering
    const hasAnswers = answers.some(a => a.optionId !== null);
    if (hasAnswers && currentQuestionIndex < surveyData.questions.length - 1) {
        e.preventDefault();
        e.returnValue = '';
    }
});