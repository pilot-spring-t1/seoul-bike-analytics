let detailChart = null;

document.addEventListener('DOMContentLoaded', function() {
    // 초기 차트 로드 (시간대별)
    switchChart('time', document.querySelector('.selector-btn.active'));
});

function switchChart(type, btn) {
    // 1. 버튼 활성화 표시 제어
    document.querySelectorAll('.selector-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    const ctx = document.getElementById('detailChart').getContext('2d');
    const title = document.getElementById('chart-title');

    // 2. 기존 차트 파괴 (메모리 관리 및 리렌더링 버그 방지)
    if (detailChart) detailChart.destroy();

    // 3. 타입에 따른 데이터 설정 (실제로는 서버에서 Fetch)
    let config = {};

    if (type === 'time') {
        title.innerText = "🕒 시간대별 이용량 추이";
        config = {
            type: 'line',
            data: {
                labels: Array.from({length: 24}, (_, i) => i + '시'),
                datasets: [{
                    label: '대여 건수',
                    data: [50, 20, 10, 5, 30, 150, 500, 900, 700, 400, 300, 350, 400, 500, 600, 800, 1200, 1500, 1100, 600, 400, 200, 100, 50],
                    borderColor: '#1abc9c',
                    tension: 0.4,
                    fill: true,
                    backgroundColor: 'rgba(26, 188, 156, 0.1)'
                }]
            }
        };
    } else if (type === 'age') {
        title.innerText = "👥 연령대별 이용 비중";
        config = {
            type: 'bar',
            data: {
                labels: ['10대', '20대', '30대', '40대', '50대', '60대+'],
                datasets: [{
                    label: '이용자 수',
                    data: [1200, 4500, 3800, 2100, 1500, 800],
                    backgroundColor: '#3498db'
                }]
            }
        };
    } else if (type === 'gender') {
        title.innerText = "🚻 성별 이용 목적 (도넛)";
        config = {
            type: 'doughnut',
            data: {
                labels: ['남성(출퇴근)', '여성(출퇴근)', '남성(운동)', '여성(운동)'],
                datasets: [{
                    data: [35, 25, 20, 20],
                    backgroundColor: ['#2c3e50', '#1abc9c', '#34495e', '#3498db']
                }]
            }
        };
    }

    // 4. 차트 생성
    config.options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom' } }
    };
    detailChart = new Chart(ctx, config);
}

function updateData() {
    // 필터 적용 시 상단 수치 변경 (애니메이션 효과 가능)
    alert("데이터를 필터링합니다...");
    document.getElementById('avg-dist').innerText = (Math.random() * 5 + 1).toFixed(1);
    document.getElementById('total-carbon').innerText = Math.floor(Math.random() * 1000);
}