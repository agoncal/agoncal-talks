FROM jboss/wildfly:10.1.0.Final

EXPOSE 8080

# Setting the Wildfly Admin console (user/pwd admin/admin)
RUN $JBOSS_HOME/bin/add-user.sh admin admin --silent
CMD $JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

COPY demo01-conference-venue.war $JBOSS_HOME/standalone/deployments/

