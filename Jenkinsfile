// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
//
// Orchestrator Pipeline - Detects changes and triggers only affected service pipelines
// Configure GitHub webhook to trigger this single pipeline
//
pipeline {
    agent any

    environment {
        // Map of directory paths to pipeline job names
        // Adjust job names to match your Jenkins job configuration
        EUREKA_JOB = 'pilotquiz-eureka-server'
        GATEWAY_JOB = 'pilotquiz-api-gateway'
        USER_JOB = 'pilotquiz-user-service'
        QUIZ_JOB = 'pilotquiz-quiz-service'
        PROGRESS_JOB = 'pilotquiz-progress-service'
        ROOT_CONFIG_JOB = 'pilotquiz-root-config'
        REACT_AUTH_JOB = 'pilotquiz-mfe-react-auth'
        ANGULAR_QUIZ_JOB = 'pilotquiz-mfe-angular-quiz'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    // Get the list of changed files between current and previous commit
                    // For PRs, compare against the base branch
                    def changes = []
                    
                    try {
                        // Get changed files from the last commit
                        changes = sh(
                            script: "git diff --name-only HEAD~1 HEAD || echo ''",
                            returnStdout: true
                        ).trim().split('\n').findAll { it }
                    } catch (Exception e) {
                        echo "Could not detect changes, building all services"
                        changes = ['backend/', 'frontend/']
                    }

                    echo "Changed files: ${changes}"

                    // Determine which services need to be built
                    env.BUILD_EUREKA = changes.any { it.startsWith('backend/eureka-server/') } ? 'true' : 'false'
                    env.BUILD_GATEWAY = changes.any { it.startsWith('backend/api-gateway/') } ? 'true' : 'false'
                    env.BUILD_USER = changes.any { it.startsWith('backend/user-service/') } ? 'true' : 'false'
                    env.BUILD_QUIZ = changes.any { it.startsWith('backend/quiz-service/') } ? 'true' : 'false'
                    env.BUILD_PROGRESS = changes.any { it.startsWith('backend/progress-service/') } ? 'true' : 'false'
                    env.BUILD_ROOT_CONFIG = changes.any { it.startsWith('frontend/root-config/') } ? 'true' : 'false'
                    env.BUILD_REACT_AUTH = changes.any { it.startsWith('frontend/mfe-react-auth/') } ? 'true' : 'false'
                    env.BUILD_ANGULAR_QUIZ = changes.any { it.startsWith('frontend/mfe-angular-quiz/') } ? 'true' : 'false'

                    // Check if infrastructure or Jenkinsfiles changed (rebuild all)
                    def infrastructureChanged = changes.any { 
                        it.startsWith('infrastructure/') || 
                        it.startsWith('jenkinsfiles/') ||
                        it == 'docker-compose.yml'
                    }

                    if (infrastructureChanged) {
                        echo "Infrastructure changes detected - marking all services for rebuild"
                        env.BUILD_EUREKA = 'true'
                        env.BUILD_GATEWAY = 'true'
                        env.BUILD_USER = 'true'
                        env.BUILD_QUIZ = 'true'
                        env.BUILD_PROGRESS = 'true'
                        env.BUILD_ROOT_CONFIG = 'true'
                        env.BUILD_REACT_AUTH = 'true'
                        env.BUILD_ANGULAR_QUIZ = 'true'
                    }

                    // Summary
                    echo """
                    Services to build:
                    - Eureka Server: ${env.BUILD_EUREKA}
                    - API Gateway: ${env.BUILD_GATEWAY}
                    - User Service: ${env.BUILD_USER}
                    - Quiz Service: ${env.BUILD_QUIZ}
                    - Progress Service: ${env.BUILD_PROGRESS}
                    - Root Config: ${env.BUILD_ROOT_CONFIG}
                    - React Auth MFE: ${env.BUILD_REACT_AUTH}
                    - Angular Quiz MFE: ${env.BUILD_ANGULAR_QUIZ}
                    """
                }
            }
        }

        stage('Build Backend Services') {
            parallel {
                stage('Eureka Server') {
                    when {
                        expression { env.BUILD_EUREKA == 'true' }
                    }
                    steps {
                        build job: "${EUREKA_JOB}", wait: true, propagate: true
                    }
                }
                stage('API Gateway') {
                    when {
                        expression { env.BUILD_GATEWAY == 'true' }
                    }
                    steps {
                        build job: "${GATEWAY_JOB}", wait: true, propagate: true
                    }
                }
                stage('User Service') {
                    when {
                        expression { env.BUILD_USER == 'true' }
                    }
                    steps {
                        build job: "${USER_JOB}", wait: true, propagate: true
                    }
                }
                stage('Quiz Service') {
                    when {
                        expression { env.BUILD_QUIZ == 'true' }
                    }
                    steps {
                        build job: "${QUIZ_JOB}", wait: true, propagate: true
                    }
                }
                stage('Progress Service') {
                    when {
                        expression { env.BUILD_PROGRESS == 'true' }
                    }
                    steps {
                        build job: "${PROGRESS_JOB}", wait: true, propagate: true
                    }
                }
            }
        }

        stage('Build Frontend Services') {
            parallel {
                stage('Root Config') {
                    when {
                        expression { env.BUILD_ROOT_CONFIG == 'true' }
                    }
                    steps {
                        build job: "${ROOT_CONFIG_JOB}", wait: true, propagate: true
                    }
                }
                stage('React Auth MFE') {
                    when {
                        expression { env.BUILD_REACT_AUTH == 'true' }
                    }
                    steps {
                        build job: "${REACT_AUTH_JOB}", wait: true, propagate: true
                    }
                }
                stage('Angular Quiz MFE') {
                    when {
                        expression { env.BUILD_ANGULAR_QUIZ == 'true' }
                    }
                    steps {
                        build job: "${ANGULAR_QUIZ_JOB}", wait: true, propagate: true
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'All triggered builds completed successfully!'
        }
        failure {
            echo 'One or more builds failed. Check individual job logs for details.'
        }
        always {
            cleanWs()
        }
    }
}
