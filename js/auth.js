
document.getElementById('login-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // 임시 로그인 로직 (나중에 API 연동)
    if (username === "admin" && password === "1234") {
        alert("로그인 성공! 대시보드로 이동합니다.");
        // JWT 토큰 저장 흉내
        localStorage.setItem("isLoggedIn", "true");
        window.location.href = "dashboard-main.html";
    } else {
        alert("아이디 또는 비밀번호가 틀렸습니다. (테스트용: admin / 1234)");
    }
});


if (document.getElementById('register-form')) {
    document.getElementById('register-form').addEventListener('submit', function(e) {
        e.preventDefault();

        const password = document.getElementById('reg-password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        // 1. 비밀번호 일치 확인
        if (password !== confirmPassword) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        // 2. 가입 처리 흉내
        alert("회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");
        window.location.href = "login.html";
    });
}