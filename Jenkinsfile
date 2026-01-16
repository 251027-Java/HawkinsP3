// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
//
// Orchestrator Pipeline - Detects changes and triggers only affected service pipelines
// Configure GitHub webhook to trigger this single pipeline
//
pipeline {
    agent any

    parameters {
        string(
            name: 'BRANCH',
            defaultValue: '',
            description: 'Branch to build (leave empty for webhook auto-detection)'
        )
    }

    environment {
        // Map of directory paths to pipeline job names
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
                script {
                    // Determine branch: use parameter if provided, otherwise detect from checkout
                    if (params.BRANCH?.trim()) {
                        env.TARGET_BRANCH = params.BRANCH.trim()
                        echo "Using parameter branch: ${env.TARGET_BRANCH}"
                    } else {
                        def detectedBranch = env.GIT_BRANCH?.replaceAll('origin/', '') ?: 'dev'
                        env.TARGET_BRANCH = detectedBranch
                        echo "Auto-detected branch: ${env.TARGET_BRANCH}"
                    }
                }
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    // Get the list of changed files between current and previous commit
                    def changes = []
                    
                    try {
                        changes = sh(
                            script: "git diff --name-only HEAD~1 HEAD || echo ''",
                            returnStdout: true
                        ).trim().split('\n').findAll { it }
                    } catch (Exception e) {
                        echo "Could not detect changes, building all services"
                        changes = ['backend/', 'frontend/']
                    }

                    echo "Changed files: ${changes}"

                    // Determine which services need to be built (source code changes)
                    env.BUILD_EUREKA = changes.any { it.startsWith('backend/eureka-server/') || it == 'jenkinsfiles/Jenkinsfile.eureka-server' } ? 'true' : 'false'
                    env.BUILD_GATEWAY = changes.any { it.startsWith('backend/api-gateway/') || it == 'jenkinsfiles/Jenkinsfile.api-gateway' } ? 'true' : 'false'
                    env.BUILD_USER = changes.any { it.startsWith('backend/user-service/') || it == 'jenkinsfiles/Jenkinsfile.user-service' } ? 'true' : 'false'
                    env.BUILD_QUIZ = changes.any { it.startsWith('backend/quiz-service/') || it == 'jenkinsfiles/Jenkinsfile.quiz-service' } ? 'true' : 'false'
                    env.BUILD_PROGRESS = changes.any { it.startsWith('backend/progress-service/') || it == 'jenkinsfiles/Jenkinsfile.progress-service' } ? 'true' : 'false'
                    env.BUILD_ROOT_CONFIG = changes.any { it.startsWith('frontend/root-config/') || it == 'jenkinsfiles/Jenkinsfile.root-config' } ? 'true' : 'false'
                    env.BUILD_REACT_AUTH = changes.any { it.startsWith('frontend/mfe-react-auth/') || it == 'jenkinsfiles/Jenkinsfile.mfe-react-auth' } ? 'true' : 'false'
                    env.BUILD_ANGULAR_QUIZ = changes.any { it.startsWith('frontend/mfe-angular-quiz/') || it == 'jenkinsfiles/Jenkinsfile.mfe-angular-quiz' } ? 'true' : 'false'

                    // Check if global infrastructure changed (rebuild all)
                    def infrastructureChanged = changes.any { 
                        it.startsWith('infrastructure/') || 
                        it == 'docker-compose.yml' ||
                        it == 'Jenkinsfile'
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

                    echo """
                    Branch: ${env.TARGET_BRANCH}
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

        stage('Build Eureka Server') {
            when {
                expression { env.BUILD_EUREKA == 'true' }
            }
            steps {
                build job: "${EUREKA_JOB}", 
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build API Gateway') {
            when {
                expression { env.BUILD_GATEWAY == 'true' }
            }
            steps {
                build job: "${GATEWAY_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build User Service') {
            when {
                expression { env.BUILD_USER == 'true' }
            }
            steps {
                build job: "${USER_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build Quiz Service') {
            when {
                expression { env.BUILD_QUIZ == 'true' }
            }
            steps {
                build job: "${QUIZ_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build Progress Service') {
            when {
                expression { env.BUILD_PROGRESS == 'true' }
            }
            steps {
                build job: "${PROGRESS_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build Root Config') {
            when {
                expression { env.BUILD_ROOT_CONFIG == 'true' }
            }
            steps {
                build job: "${ROOT_CONFIG_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build React Auth MFE') {
            when {
                expression { env.BUILD_REACT_AUTH == 'true' }
            }
            steps {
                build job: "${REACT_AUTH_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }

        stage('Build Angular Quiz MFE') {
            when {
                expression { env.BUILD_ANGULAR_QUIZ == 'true' }
            }
            steps {
                build job: "${ANGULAR_QUIZ_JOB}",
                      parameters: [string(name: 'BRANCH', value: env.TARGET_BRANCH)],
                      wait: true, propagate: true
            }
        }
    }

    post {
        success {
            echo "All triggered builds completed successfully for branch: ${env.TARGET_BRANCH}"
        }
        failure {
            echo 'One or more builds failed. Check individual job logs for details.'
        }
        always {
            cleanWs()
        }
    }
}
