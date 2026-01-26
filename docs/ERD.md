# Entity Relationship Diagram

## Overview

The Pilot Quiz Platform uses a **database-per-service** pattern with three PostgreSQL databases.

---

## Complete ERD

```mermaid
erDiagram
    %% User Service Database
    USERS ||--o| PILOT_PROFILES : has
    
    USERS {
        bigint id PK
        varchar email UK
        varchar password
        varchar first_name
        varchar last_name
        enum role
        timestamp created_at
        timestamp updated_at
    }
    
    PILOT_PROFILES {
        bigint id PK
        bigint user_id FK,UK
        enum current_rating
        enum target_rating
        int flight_hours
        varchar preferred_categories
    }

    %% Quiz Service Database
    QUIZZES ||--o{ QUIZ_QUESTIONS : contains
    QUESTIONS ||--o{ QUIZ_QUESTIONS : included_in
    QUESTIONS ||--o{ ANSWERS : has
    QUESTIONS }o--o{ QUESTION_CATEGORIES : belongs_to
    CATEGORIES ||--o{ QUESTION_CATEGORIES : categorizes
    CATEGORIES ||--o| CATEGORIES : parent_of

    QUESTIONS {
        bigint id PK
        text question_text
        text explanation
        enum difficulty
        enum rating_type
        timestamp created_at
        timestamp updated_at
    }

    ANSWERS {
        bigint id PK
        bigint question_id FK
        varchar answer_text
        boolean is_correct
    }

    QUIZZES {
        bigint id PK
        varchar title
        text description
        enum rating_type
        int time_limit_minutes
        timestamp created_at
    }

    QUIZ_QUESTIONS {
        bigint quiz_id PK,FK
        bigint question_id PK,FK
        int question_order
    }

    CATEGORIES {
        bigint id PK
        varchar name UK
        text description
        bigint parent_id FK
    }

    QUESTION_CATEGORIES {
        bigint question_id PK,FK
        bigint category_id PK,FK
    }

    %% Progress Service Database
    QUIZ_ATTEMPTS ||--o{ QUESTION_RESPONSES : contains
    USER_ACHIEVEMENTS }o--o{ ACHIEVEMENTS : earned

    QUIZ_ATTEMPTS {
        bigint id PK
        bigint user_id
        bigint quiz_id
        int score
        int total_questions
        int time_spent_seconds
        timestamp completed_at
    }

    QUESTION_RESPONSES {
        bigint id PK
        bigint attempt_id FK
        bigint question_id
        bigint selected_answer_id
        boolean is_correct
    }

    USER_PROGRESS {
        bigint id PK
        bigint user_id
        bigint category_id
        int total_attempts
        int correct_count
        timestamp last_attempt
    }

    ACHIEVEMENTS {
        bigint id PK
        varchar name UK
        text description
        varchar criteria
        varchar icon_url
    }

    USER_ACHIEVEMENTS {
        bigint user_id PK
        bigint achievement_id PK,FK
        timestamp earned_at
    }
```

---

## Database Schemas by Service

### User Service Database (Port 5432)

| Table | Columns | Description |
|-------|---------|-------------|
| `users` | 7 | User authentication data |
| `pilot_profiles` | 5 | Aviation-specific user info |

**Table Count:** 2

---

### Quiz Service Database (Port 5433)

| Table | Columns | Description |
|-------|---------|-------------|
| `questions` | 7 | Quiz question content |
| `answers` | 4 | Answer options per question |
| `quizzes` | 5 | Quiz metadata |
| `quiz_questions` | 3 | Quiz-Question M:M join |
| `categories` | 4 | Topic categories |
| `question_categories` | 2 | Question-Category M:M join |

**Table Count:** 6

---

### Progress Service Database (Port 5434)

| Table | Columns | Description |
|-------|---------|-------------|
| `quiz_attempts` | 7 | Completed quiz records |
| `question_responses` | 5 | Individual question answers |
| `user_progress` | 6 | Category-level progress |
| `achievements` | 5 | Achievement definitions |
| `user_achievements` | 3 | User-Achievement M:M join |

**Table Count:** 5

---

## Normalization

All tables are normalized to **3NF**:

- 1NF: All columns contain atomic values
- 2NF: All non-key columns depend on the full primary key
- 3NF: No transitive dependencies between non-key columns

---

## Many-to-Many Relationships

| Relationship | Join Table | Description |
|--------------|------------|-------------|
| Quiz ↔ Question | `quiz_questions` | Questions can appear in multiple quizzes |
| Question ↔ Category | `question_categories` | Questions can have multiple categories |
| User ↔ Achievement | `user_achievements` | Users can earn multiple achievements |

**Total M:M Relationships:** 3 (exceeds P3 requirement of 2)

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
