@Library('global@master') _

node (){

    dir (env.BUILD_ID){
		checkout scm: [$class: 'GitSCM', userRemoteConfigs: [[credentialsId: 'GIT_CRED', url: env.gitlabSourceRepoHttpUrl]], branches: [[name: env.gitlabSourceBranch]]]
        
        def serviceName = "${env.gitlabSourceRepoName}"
		def pom = readMavenPom file: 'pom.xml'
        def version = pom.version
		def dockerNexus = "nexus.corp.bankbtpn.co.id:50001"
        def dockerRegistry = "docker-registry-default.ms.corp.bankbtpn.co.id"
        def domain = "ms.corp.bankbtpn.co.id"
        def ocpUrl = "https://ocp.corp.bankbtpn.co.id:8443"
        def nsService = "cakra2/ms-websocket"
        def namespace = "cakra-staging"
        def nexusCred = "NEXUS_REGISTRY"
        def ocpCredential = "OCP_NEW_PROD"
        
        currentBuild.displayName = "#${BUILD_NUMBER}, ${version}"
    
        stage ("build artifact"){
        		dockerRun image: "nexus.corp.bankbtpn.co.id:50001/openshift/build-artifact:latest", cmd:"mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install --settings maven-settings.xml"
	        	configFileProvider([configFile(fileId: 'vars', variable: 'vars')]){
			    	dockerRun vars: "${vars}", image: "nexus.corp.bankbtpn.co.id:50001/openshift/sonar-runner:latest", cmd:""
			}
        }
        
        stage ("build image"){
            dockerBuild image: "${nsService}:${version}", workdir: pwd()
        }
    
       	stage ("tag & push"){
	        dockerTag source: "${nsService}:${version}", to: "${dockerRegistry}/${namespace}/${serviceName}:${version}"
            dockerTag source: "${nsService}:${version}", to: "${dockerNexus}/${nsService}:${version}"
	        dockerPush image: "${dockerNexus}/${nsService}:${version}", to: dockerNexus, credentialsId: nexusCred
		}
	             
		stage ("deploy"){
	      	ocpDeploy session: "${namespace}-${serviceName}-${BUILD_NUMBER}", namespace: namespace, serviceName: serviceName, ocpUrl: ocpUrl, ocpCredential: ocpCredential, dockerRegistry: dockerRegistry, image: "${dockerRegistry}/${namespace}/${serviceName}:${version}", template: "deploy/template.yml", variable: "deploy/staging.env", parameters: "domain=${domain} namespace=${namespace} version=${version}"
		}              
    }
 }