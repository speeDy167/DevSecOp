pipeline {
    agent any
    
    tools {
        maven 'maven3'
        jdk 'jdk17'
    }
    
    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        DOCKER_CREDENTIALS_ID = 'docker-cred'
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/speeDy167/DevSecOp.git'
            }
        }
        
        stage('Compile') {
            steps {
                sh "mvn compile"
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh "mvn test -DskipTests=true"
            }
        }
                
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectKey=speedy -Dsonar.projectName=speedy \
                    -Dsonar.java.binaries=. '''
                }
            }
        }
        
        stage('Dependencies Check OWASP') {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }
    
    
        // stage('Unzip and Check OWASP Dependencies') {
        //     steps {
        //         script {
        //             def targets = [
        //                 [file: 'ransomeware_7ev3n.zip', dest: 'target1', password: 'mysubsarethebest'],
        //                 [file: 'trojans_FakeActivation.zip', dest: 'target2', password: 'mysubsarethebest'],
        //                 [file: 'nodejs.zip', dest: 'target3', password: ''],
        //             ]

        //             for (target in targets) {
        //                 if (target.file.endsWith('.zip')) {
        //                     if (target.password) {
        //                         sh "unzip -o -P ${target.password} ${target.file} -d ${target.dest}"
        //                     } else {
        //                         sh "unzip -o ${target.file} -d ${target.dest}"
        //                     }
        //                 } else{
        //                 }
                        
        //                 if (fileExists("${target.dest}/package-lock.json")) {
        //                     dir("${target.dest}") {
        //                         sh "npm install"
        //                     }
        //                 }
                        
        //                 dependencyCheck additionalArguments: "--scan ${target.dest} --disableAssembly --exclude **/package-lock.json", odcInstallation: 'DC'
        //             }
        //         }
        //         dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        //     }
        // }
    
        stage('Build with Maven') {
            steps {
                sh "mvn package -DskipTests=true"
            }
        }
        
        stage('Deploy to NEXUS') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-maven', jdk: 'jdk17', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                    sh "mvn deploy -DskipTests=true"
                }
            }
        }
        
        stage('Build and Tag Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker build -t trongphucphan/speedy:latest -f docker/Dockerfile ."
                    }
                }
            }
        }
        
        stage('Trivy Scan') {
            steps {
                sh "trivy image --scanners vuln  trongphucphan/speedy:latest > trivy-report.txt"
            }
        }
        
        stage('Push Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push trongphucphan/speedy:latest"
                    }
                }
            }
        }
        
        stage('Archive Report') {
            steps {
                archiveArtifacts artifacts: 'trivy-report.txt', allowEmptyArchive: true
            }
        }
        
        // stage('K8s deploy') {
        //     steps {
        //         withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'k8s-token', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://192.168.5.142:6443') {
        //             sh "kubectl apply -f deploymentservice.yml -n webapps"
        //             sh "kubectl get svc -n webapps"
        //         }
        //     }
        // }
        
    }
}
