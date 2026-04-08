컨트롤러 추가<br>
@GetMapping  <br>
/                         -> login  <br>
/signup                   -> signup <br>

/dashboard                -> dashboard/user-dashboard<br>

/analysis/summary         -> analysis/summary<br>
/analysis/detail          -> analysis/detail<br>

/admin/dashboard          -> admin/admin-dashboard<br>
/admin/data               -> admin/data-manage<br>

/notifications            -> notification/list<br>

/error/403                -> error/403<br>
/error/404                -> error/404<br>


국제화 적용<br>
src/main/resources/i18n 폴더 추가 message_en.properties와 message_ko.properties 파일 추가<br>
application.properties에 국제화 관련 설정 코드 추가(7,8줄), WebMvcConfig.java 코드 추가(seoulbike/config)<br>

웹소켓<br>
MyWebeSocketHandler.java(웹소켓 서버)를 seoulbike.websocket.handler에 추가<br>
WebSocketConfig.java(웹소켓 설정) 추가 seoulbike.config에 추가<br>
