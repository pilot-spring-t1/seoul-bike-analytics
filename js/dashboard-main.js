document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('mainChart');
    
    const labels = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
    const data = [150000, 180000, 250000, 420000, 580000, 620000, 590000, 610000, 720000, 550000, 320000, 200000];

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '월별 대여 건수 (건)',
                data: data,
                borderColor: '#1abc9c',
                borderWidth: 3,
                backgroundColor: 'rgba(26, 188, 156, 0.1)',
                fill: true,
                tension: 0.4,
                pointRadius: 4,
                pointBackgroundColor: '#1abc9c'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // 부모 컨테이너(chart-wrapper) 높이에 맞춤
            animation: {
                duration: 1000
            },
            plugins: {
                legend: { display: true, position: 'bottom' }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#f0f0f0' }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
});