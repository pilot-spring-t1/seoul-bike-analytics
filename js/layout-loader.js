document.addEventListener("DOMContentLoaded", function() {
    // 1. 사이드바 영역 찾기
    const sidebar = document.getElementById('sidebar');
    
    if (sidebar) {
        // 2. 외부 sidebar.html 파일 읽어오기
        fetch('layout/sidebar.html')
            .then(response => response.text())
            .then(data => {
                sidebar.innerHTML = data;
                
                // 3. 현재 페이지에 맞는 메뉴 하이라이트 활성화
                const currentPage = window.location.pathname.split("/").pop();
                const links = sidebar.querySelectorAll('a');
                links.forEach(link => {
                    if (link.getAttribute('href') === currentPage) {
                        link.style.backgroundColor = "#34495e";
                        link.style.color = "#1abc9c";
                    }
                });
            })
            .catch(err => console.error("사이드바 로드 실패:", err));
    }
});