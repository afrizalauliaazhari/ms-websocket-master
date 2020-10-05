@Library('global@master') _

node (){

	deployDescription deployDesc: deployDesc
    dir (env.BUILD_ID){
    		def serviceName = "ms-websocket"
	    def nsService = "cakra2/ms-websocket"
	    def dockerRegistry = "docker-registry-default.ms.corp.bankbtpn.co.id"
	    def dockerNexus = "nexus.corp.bankbtpn.co.id:50001"
	    def domain = "ms.corp.bankbtpn.co.id"
	    def ocpUrl = "https://ocp.corp.bankbtpn.co.id:8443"
	    def ocpCredential = "OCP_NEW_PROD"
	    def namespace = "cakra"
	    
	    currentBuild.displayName = "#${BUILD_NUMBER}, ${version}"
	    
        stage ("tag & push"){
			dockerPull image: "${dockerNexus}/${nsService}:${version}"
			dockerTag source: "${dockerNexus}/${nsService}:${version}", to: "${dockerRegistry}/${namespace}/${serviceName}:${version}"
        }
        
        stage ("deploy"){
			checkout scm: [$class: 'GitSCM', userRemoteConfigs: [[credentialsId: 'GIT_CRED', url: "https://git.ecommchannels.com/cakra2/ms-websocket.git"]], branches: [[name: "master"]]]  
			ocpDeploy session: "${namespace}-${serviceName}-${BUILD_NUMBER}", namespace: namespace, serviceName: serviceName, ocpUrl: ocpUrl, ocpCredential: ocpCredential, dockerRegistry: dockerRegistry, image: "${dockerRegistry}/${namespace}/${serviceName}:${version}", template: "deploy/template-prod.yml", variable: "deploy/prod.env", parameters: "domain=${domain} namespace=${namespace} version=${version}"
        }
    }
}