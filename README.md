# Comps-Microservices
reproducing results

To reproduce the results from this project, the developer must have access to an AWS account that has ECS, ECR, EC2 (ALB), and API gateway enabled, as well as an Okta developer account. The project repo can be found here.

After the Okta developer account is created: https://developer.okta.com/signup/, a new web server application must be created in the Okta application management UI. Click the “Create App Integration” button under the Applications section on the left Navbar. 


<img src="https://intellivest-images-bucket.s3.amazonaws.com/okta-App-page.png" width="auto" height="auto"> 




This will allow you to choose a type of application to create; this project uses OIDC - OpenID Connect, a “Token-based OAuth 2.0 authentication for Single Sign-On through API endpoints.” The type of application that we need to create is a “web application.”
 

 
<img src="https://intellivest-images-bucket.s3.amazonaws.com/okta-create-app-popup.png" width="auto" height="auto"> 

Since we are not using the Okta login card we can delta the options for trusted origin, login redirect, etc and we can add the name of our new application and click save. Now we will need to collect the Okta Issuer values for use in our application to verify and issue tokens. First navigate to your application in the Okta application catalog.
 
 
 

 <img src=" https://intellivest-images-bucket.s3.amazonaws.com/okta-created-app-dash.png" width="auto" height="auto"> 

 
 
  
 
 
 
Once you reach the page for your application, take note of the Client Secret, Client Id and Okta domain which you will need to add to your spring application properties file. Once these properties are added to your spring app, it will communicate with Okta to validate tokens. 

 
  <img src="https://intellivest-images-bucket.s3.amazonaws.com/properites-file.png" width="auto" height="auto"> 

 
 
 
 
The Authorization server requires one additional value, the “okta.authorization” token. This is the Okta API key that is required to add new users to the Okta registry. To get this key navigate to the API tab under security in the Okta UI. Create a new token, and use that token as the Okta 
authorization.

 
 
 
 
 
 
   <img src="https://intellivest-images-bucket.s3.amazonaws.com/okta-api-token.png" width="auto" height="auto"> 
 
 
 
 
 
 
To add a database connection to the service you must register a MySql database on your local machine, with docker, or preferably in a cloud environment such as AWS, Digital Ocean, GCP, Azure, etc. A new database will need to be established for each service, and the tables can be initialized in the database using the provided SQL scripts in the schema folder. Once the databases are initialized with the correct schema, you can obtain the username, password, and jdbc connection string and add it to the application properties to allow the service to connect to a data source. 
Now that the service has the required configs for the database and Okta, you can execute the service tests to validate that the application is working correctly with ./gradlew test while in the directory of a given service. Once you have verified the behavior of the app, you can begin to set up the CI pipeline to deliver tested images to the Amazon ECR. Obtain the AWS Access Key Id and the Key Secret Content from the AWS Console: https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html. Ensure that the Keys are for an IAM User and not the Root user of the account. If you have not already Forked the repository do so and make it private or add additional configs to hide and inject your okta secrets on runtime such as Github secrets, which i use for the AWS secrets, or use another secret provider like Hashicorp Vault. Configure the Github secrets for your new repo: https://github.com/Azure/actions-workflow-samples/blob/master/assets/create-secrets-for-GitHub-workflows.md using the AWS keys. Next create a private repo for each service: https://docs.aws.amazon.com/AmazonECR/latest/userguide/repository-create.html. Make sure to add the Endpoint for this repo in the github actions workflow file for each service. To find this info navigate to the repo page for each service and click “view push commands''. 
 

 
  
 
 
 
 
Once the GitHub pipeline is configured successfully you should be able to see the actions occur and push images to your ECR upon completion. 

 
 
 <img src="https://intellivest-images-bucket.s3.amazonaws.com/ecr-repo.png" width="auto" height="auto"> 

 
 
 
 
 
Now that your services have been successfully built, tested, and sent to the ECR we can use its image to deploy instances of it in ECS. Each of these service must pull from an ECS task definition that you must have defined based upon the main tag from the desired ECR repo: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-task-definition.html. Next navigate to the ECS console and create a service for each microservice:
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-service.html.
 

 
 
 
This will deploy an ECS service for each microservice but if we want to add load balancing for each service we will need to define an ALB for each service before setting it up and define it within the initial ECS service configurations: https://aws.amazon.com/premiumsupport/knowledge-center/create-alb-auto-register/. This process will automatically create a listener in the ALB pointing to the ECS service group.  
 
 
 
 
 <img src="https://intellivest-images-bucket.s3.amazonaws.com/alb-screen.png" width="auto" height="auto"> 

 
 
 
 
The final step is to connect the AWS API Gateway to each of the load balancers. Navigate to AWS API Gateway and set up a new HTTP API, creating new endpoints for each of the services and adding its corresponding url using the load balancer, a reverse proxy to the ECS group.  

 
 
 
The microservices are written in Java using the Spring Boot Framework to implement 
 
 
 
All software and versions for each service are listed within its respective build.gradle file and complete dependencies trees for a service can be generated using ./gradlew dependencies within a service dir, but this a broad list of technologies and versions (if applicable) that were used in this project: 
AWS (API gateway (HTTP), ECS, ECR, ALB, ACM, Route53)
Spring-orm 5.2.2.RELEASE
Spring Framework: ( spring-boot-starter-web, spring-boot-starter-data-jpa,  spring-boot-starter-security, spring-boot-starter-actuator, spring-boot-devtools, spring-security-test, spring-boot-starter-test) - v2.4.5
H2 database v1.4.199
Apache Tomcat v7.0.14
Hibernate-Core v5.4.30
Okta-spring-boot-starter:2.0.1
Mysql-connector-java  v8.0.23
OkHTTP3 v4.2.0
lombok v1.18.20
