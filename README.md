# QA-java-diplom-2
API tests for the service https://stellarburgers.nomoreparties.site/

Documentation. You can find the documentation at https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf

To generate Allure Report you should perform following steps:

git clone git@github.com:KsushaMalysheva/QA-java-diplom-2.git

mvn clean test

allure serve target/surefire-reports/
