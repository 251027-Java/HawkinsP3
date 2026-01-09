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

- [ ] User can see list of available quizzes
- [ ] Quizzes can be filtered by rating type
- [ ] Quizzes show title, description, and question count

**Status:** 🔲 Pending - Quiz Service

---

### US-2.2: Take a Quiz

**As a** user  
**I want to** take a practice quiz  
**So that** I can test my knowledge  

**Acceptance Criteria:**

- [ ] User can start a quiz
- [ ] Questions displayed one at a time
- [ ] User can select an answer
- [ ] User can navigate between questions
- [ ] Quiz can be submitted

**Status:** 🔲 Pending - Quiz Service + Angular MFE

---

### US-2.3: View Quiz Results

**As a** user  
**I want to** see my quiz results  
**So that** I can understand what I got right/wrong  

**Acceptance Criteria:**

- [ ] Score displayed after quiz completion
- [ ] Each question shows correct answer
- [ ] Explanation shown for each question

**Status:** 🔲 Pending - Progress Service + Angular MFE

---

### US-2.4: Practice by Category

**As a** user  
**I want to** select specific categories to practice  
**So that** I can focus on my weak areas  

**Acceptance Criteria:**

- [ ] User can select one or more categories
- [ ] Questions filtered by selected categories
- [ ] Random question selection from pool

**Status:** 🔲 Pending - Quiz Service

---

## Epic 3: Admin - Question Management

### US-3.1: Bulk Upload Questions

**As an** admin  
**I want to** upload questions via CSV  
**So that** I can quickly add many questions  

**Acceptance Criteria:**

- [ ] Admin can download CSV template
- [ ] Admin can upload CSV file
- [ ] Validation errors reported
- [ ] Successfully imported count shown

**Status:** 🔲 Pending - Quiz Service

---

### US-3.2: Manage Individual Questions

**As an** admin  
**I want to** create, edit, and delete questions  
**So that** I can keep content up to date  

**Acceptance Criteria:**

- [ ] Admin can create new question with answers
- [ ] Admin can edit existing question
- [ ] Admin can delete question
- [ ] Admin can assign categories to questions

**Status:** 🔲 Pending - Quiz Service

---

## Epic 4: Progress Tracking

### US-4.1: View Progress Dashboard

**As a** user  
**I want to** see my overall progress  
**So that** I know how prepared I am  

**Acceptance Criteria:**

- [ ] Display overall score percentage
- [ ] Show progress by category
- [ ] Show recent quiz attempts
- [ ] Visual charts for progress

**Status:** 🔲 Pending - Progress Service + React MFE

---

### US-4.2: Identify Weak Areas

**As a** user  
**I want to** see which categories I struggle with  
**So that** I can focus my study  

**Acceptance Criteria:**

- [ ] Categories ranked by performance
- [ ] Low-performing categories highlighted
- [ ] Suggested quizzes for weak areas

**Status:** 🔲 Pending - Progress Service

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
