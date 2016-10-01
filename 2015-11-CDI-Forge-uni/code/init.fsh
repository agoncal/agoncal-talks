
#  ################################  #
#  Creates the expenses web project  #
#  ################################  #

project-new --named expenses-web --topLevelPackage org.expenses.web --type war ;

# Sets the project to use Java EE 7 artifacts
# #################

jpa-setup --jpaVersion 2.1 --persistenceUnitName expenses-pu ;
cdi-setup --cdiVersion 1.1 ;
faces-setup --facesVersion 2.2 ;
servlet-setup --servletVersion 3.1 ;
rest-setup --jaxrsVersion 2.0 ;


# Creates the entities
# #################

java-new-enum --named UserRole --targetPackage ~.model ;
java-new-enum-const USER ;
java-new-enum-const ADMIN ;

jpa-new-entity --named User --tableName t_user ;
jpa-new-field --named login ;
jpa-new-field --named password ;
jpa-new-field --named name ;
jpa-new-field --named email ;
jpa-new-field --named role --type ~.model.UserRole ;


jpa-new-entity --named Conference --tableName t_conference ;
jpa-new-field --named name ;
jpa-new-field --named date --type java.util.Date ;
jpa-new-field --named country ;
jpa-new-field --named city ;

java-new-enum --named Currency --targetPackage ~.model ;
java-new-enum-const USD ;
java-new-enum-const EURO ;

java-new-enum --named ExpenseType --targetPackage ~.model ;
java-new-enum-const HOTEL ;
java-new-enum-const RESTAURANT ;
java-new-enum-const TRAIN ;
java-new-enum-const FLIGHT ;

jpa-new-entity --named Expense --tableName t_expense ;
jpa-new-field --named description ;
jpa-new-field --named date --type java.util.Date ;
jpa-new-field --named amount --type java.lang.Float ;
jpa-new-field --named expenseType --type ~.model.ExpenseType ;
jpa-new-field --named currency --type ~.model.Currency ;

jpa-new-entity --named Reimbursement --tableName t_reimbursement ;
jpa-new-field --named date --type java.util.Date ;
jpa-new-field --named expenses --type ~.model.Expense --relationshipType One-to-Many ;
jpa-new-field --named currency --type ~.model.Currency ;
jpa-new-field --named user --type ~.model.User --relationshipType Many-to-One ;
jpa-new-field --named conference --type ~.model.Conference --relationshipType Many-to-One ;


# Scaffolds a JSF application
# ###########################

scaffold-generate --provider Faces --targets org.expenses.web.model.* --webRoot admin ;


# Scaffolds REST endpoints
# ###########################
 
rest-generate-endpoints-from-entities --targets org.expenses.web.model.Conference ;

