document.addEventListener('DOMContentLoaded', function() {
    const dataList = [
        { date: '2025-05-01', id: 'ST-101', name: '마포구청역', count: 450, dist: 12500 },
        { date: '2025-05-01', id: 'ST-202', name: '여의나루역', count: 890, dist: 35400 },
        { date: '2025-05-01', id: 'ST-305', name: '뚝섬유원지역', count: 720, dist: 28100 },
        { date: '2025-05-01', id: 'ST-408', name: '광화문역', count: 310, dist: 8900 }
    ];

    const tbody = document.getElementById('data-list');
    dataList.forEach(item => {
        const row = `<tr>
            <td>${item.date}</td>
            <td>${item.id}</td>
            <td>${item.name}</td>
            <td>${item.count}</td>
            <td>${item.dist.toLocaleString()}</td>
            <td><span class="status-badge">정상적재</span></td>
        </tr>`;
        tbody.innerHTML += row;
    });
});

function downloadReport(type) {
    alert(type.toUpperCase() + " 리포트 생성을 시작합니다. (백엔드 파일 생성 API 호출)");
}