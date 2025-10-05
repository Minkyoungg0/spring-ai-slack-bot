
# 🍀 Spring AI 금융 알림봇

**“매일 한 줄로 금융 인사이트와 행운을 전하는 Slack 알림 서비스”**  
> Google Gemini API를 Spring AI로 연동하여,  
> 매일 새로운 금융 용어와 모티베이션 문구를 자동 생성·전송하는 프로젝트입니다.

---

## 🎯 목적  
Spring AI를 활용해 OpenAPI 호출보다 간결하게 AI 응답을 받아  
Slack으로 자동 알림을 전송하는 금융 인사이트 서비스입니다.

---

## ⚡핵심 기능  
- **Gemini 모델 기반** ‘오늘의 금융 용어’ 자동 생성  
- **Temperature 조절**로 매일 새로운 문장 생성  
- **Slack Webhook**을 통한 실시간 메시지 전송  
- **스케줄러**를 이용한 매일 오전 9시 자동 알림 발송  

---

### 🧩 기술 스택 & 개발 환경  

**Framework** : Spring Boot 3.4.4  
**AI Library** : Spring AI 1.1.0-M3  
**Model Provider** : Google Gemini Developer API (`gemini-2.5-flash`)  
**Messaging** : Slack Incoming Webhook
**Build Tool** : Gradle (Groovy)
**Language** : Java 21

---

## ⚙️ 구현 과정

### 1️⃣ **Spring AI 세팅**
```groovy
dependencyManagement {
    imports { mavenBom "org.springframework.ai:spring-ai-bom:1.1.0-M3" }
}
dependencies {
    implementation 'org.springframework.ai:spring-ai-starter-model-google-genai'
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

```yaml
spring:
  ai:
    google:
      genai:
        api-key: ${GOOGLE_GENAI_API_KEY}
        chat:
          options:
            model: gemini-2.5-flash
```

### 2️⃣ **메시지 생성**
```java
@Service
@RequiredArgsConstructor
public class FinanceDailyService {

    private final ChatClient.Builder chatClientBuilder;

    public String composeMessage() {
        var options = GoogleGenAiChatOptions.builder().temperature(0.8).build();

        String finance = chatClientBuilder.build()
                .prompt()
                .user("""
                        너는 매일 새로운 문체와 관점으로 새로운 금융 트렌드 용어와 짧은 행운 문장을 작성하는 작가야.
                        오늘은 무작위로 다른 스타일, 다른 주제의 금융 용어와 메시지를 만들어줘.
                        형식:
                        🧾 오늘의 금융 용어
                        > {이모지} {용어} - {한 줄 설명}
                        🍀 행운의 한줄
                        > {이모지} {한국어로 14~24자, 이모지 1개 포함, 동기부여 문장 1줄만 출력}
                        예:
                        🧾 *오늘의 금융 용어*
                        > 💰 *이자* — 은행에 돈을 맡기거나 빌릴 때 주고받는 돈의 대가입니다.
                        🍀 *행운의 한줄*
                        > ✨ 작은 실험이 변화를 만든다.
                        이외의 말은 하지 마세요.
                        """)
                .options(options)
                .call()
                .content();

        return finance;
    }
}
```

### 3️⃣ **Slack 메시지 전송**
```java
public void sendText(String text) {
        if (!enabled) return;
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.err.println("Slack disabled: webhook URL is empty");
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("text", text);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            rest.postForEntity(webhookUrl, entity, String.class);
        } catch (RestClientException e) {
            System.err.println("Slack send failed: " + e.getMessage());
        }
    }
```

### 4️⃣ **테스트 엔드포인트**
```java
@RestController
@RequiredArgsConstructor
public class NotifyController {

    private final FinanceDailyService service;
    private final SlackClient slack;

    @GetMapping("/notify/test")
    public String test() {
        String msg = service.composeMessage();
        slack.sendText(msg);
        return msg;
    }
}
```

### 5️⃣ **반복 알람 설정 적용**
```java
@Component
@RequiredArgsConstructor
public class NotifyScheduler {

    private final FinanceDailyService financeDailyService;
    private final SlackClient slack;

    @Scheduled(cron = "${app.notify.cron}", zone = "Asia/Seoul")
    public void sendDaily() {
        String message = financeDailyService.composeMessage();
        slack.sendText(message);
    }
}
```

---

## 🧾 결과 화면

<img width="836" height="689" alt="image" src="https://github.com/user-attachments/assets/dd841907-4019-496c-90a5-e99ca4421bec" />


---

## 💡 트러블슈팅

| 문제 | 원인 | 해결 방법 |
|------|------|-----------|
| Spring AI 의존성 관련 오류 (`Could not find ...`, `NoSuchFieldError ...`) | spring-ai 모듈 버전이 서로 달라 충돌 발생 | `spring-ai-bom:1.1.0-M3` 을 사용하여 모든 Spring AI 의존성 버전을 통일 |
| API Key 누락 | 환경변수 인식 안됨 | Run Configuration에 `GOOGLE_GENAI_API_KEY` 직접 추가 |
| `Multiple AI models found` | 프로젝트 생성 시 여러 AI 스타터를 동시에 등록함 | 실제 사용할 `spring-ai-starter-model-google-genai`만 남기고 나머지 의존성 제거 |

---

## 🚀 프로젝트 회고

이번 프로젝트를 통해 **Spring AI의 가장 큰 강점은 단순함과 확장성**이라는 것을 직접 체감할 수 있었습니다.  
기존처럼 OpenAPI를 직접 호출할 때보다 코드가 훨씬 간결해졌고,  
모델이나 옵션을 손쉽게 교체할 수 있어 개발 효율성이 높았습니다.  

앞으로는 사용자 맞춤형 프롬프트를 적용하거나,  
실제 금융 데이터를 활용한 **RAG 기반 인사이트 생성**,  
그리고 웹 대시보드 형태의 “오늘의 금융 봇” 서비스로 발전시키는 것을 목표로 하고 있습니다.
