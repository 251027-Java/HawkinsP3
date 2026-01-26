# User Stories

## Epic 1: User Authentication & Profiles

### US-1.1: User Registration

**As a** pilot preparing for a rating exam  
**I want to** create an account  
**So that** I can track my progress and save my quiz history  

**Acceptance Criteria:**

- [x] User can register with email, password, first name, last name
- [x] Password must be at least 8 characters
- [x] Email must be unique
- [x] JWT token returned on successful registration
- [x] Default pilot profile created (Student rating)

**Status:** ✅ Implemented in User Service

---

### US-1.2: User Login

**As a** registered user  
**I want to** log in to my account  
**So that** I can access my quizzes and progress  

**Acceptance Criteria:**

- [x] User can login with email and password
- [x] JWT token returned on successful login
- [x] Invalid credentials return 401 error

**Status:** ✅ Implemented in User Service

---

### US-1.3: Profile Management

**As a** logged-in user  
**I want to** update my pilot profile  
**So that** I can set my current rating and target rating  

**Acceptance Criteria:**

- [x] User can view their profile
- [x] User can update first name, last name
- [x] User can set current rating (Student, Private, Instrument, Commercial, ATP)
- [x] User can set target rating
- [x] User can set flight hours
- [x] User can set preferred quiz categories

**Status:** ✅ Implemented in User Service

---

## Epic 2: Quiz Management

### US-2.1: Browse Quizzes

**As a** user  
**I want to** browse available quizzes  
**So that** I can choose what to study  

**Acceptance Criteria:**

- [x] User can see list of available quizzes
- [x] Quizzes can be filtered by rating type
- [x] Quizzes show title, description, and question count

**Status:** ✅ Implemented in Quiz Service + Angular MFE

---

### US-2.2: Take a Quiz

**As a** user  
**I want to** take a practice quiz  
**So that** I can test my knowledge  

**Acceptance Criteria:**

- [x] User can start a quiz
- [x] Questions displayed one at a time
- [x] User can select an answer
- [x] User can navigate between questions
- [x] Quiz can be submitted

**Status:** ✅ Implemented in Quiz Service + Angular MFE (quiz-player component)

---

### US-2.3: View Quiz Results

**As a** user  
**I want to** see my quiz results  
**So that** I can understand what I got right/wrong  

**Acceptance Criteria:**

- [x] Score displayed after quiz completion
- [x] Each question shows correct answer
- [x] Explanation shown for each question

**Status:** ✅ Implemented in Progress Service + Angular MFE

---

### US-2.4: Practice by Category

**As a** user  
**I want to** select specific categories to practice  
**So that** I can focus on my weak areas  

**Acceptance Criteria:**

- [x] User can select one or more categories
- [x] Questions filtered by selected categories
- [x] Random question selection from pool

**Status:** ✅ Implemented in Quiz Service

---

## Epic 3: Admin - Question Management

### US-3.1: Bulk Upload Questions

**As an** admin  
**I want to** upload questions via CSV  
**So that** I can quickly add many questions  

**Acceptance Criteria:**

- [x] Admin can download CSV template
- [x] Admin can upload CSV file
- [x] Validation errors reported
- [x] Successfully imported count shown

**Status:** ✅ Implemented in Quiz Service (QuestionController /bulk and /template endpoints)

---

### US-3.2: Manage Individual Questions

**As an** admin  
**I want to** create, edit, and delete questions  
**So that** I can keep content up to date  

**Acceptance Criteria:**

- [x] Admin can create new question with answers
- [x] Admin can edit existing question
- [x] Admin can delete question
- [x] Admin can assign categories to questions

**Status:** ✅ Implemented in Quiz Service + Angular MFE (admin components)

---

## Epic 4: Progress Tracking

### US-4.1: View Progress Dashboard

**As a** user  
**I want to** see my overall progress  
**So that** I know how prepared I am  

**Acceptance Criteria:**

- [x] Display overall score percentage
- [x] Show progress by category
- [x] Show recent quiz attempts
- [x] Visual charts for progress

**Status:** ✅ Implemented in Progress Service + Angular MFE (progress component)

---

### US-4.2: Identify Weak Areas

**As a** user  
**I want to** see which categories I struggle with  
**So that** I can focus my study  

**Acceptance Criteria:**

- [x] Categories ranked by performance
- [x] Low-performing categories highlighted
- [ ] Suggested quizzes for weak areas

**Status:** 🔄 Partially Implemented - Analytics endpoint exists, suggestion feature pending

---

## Legend

| Status | Meaning |
|--------|---------|
| ✅ | Implemented |
| 🔄 | In Progress |
| 🔲 | Pending |

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
