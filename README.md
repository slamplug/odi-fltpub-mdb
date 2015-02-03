# odi-fltpub-mdb

Description
---------------

Message Driven Bean to be deployed in WebLogic that connects to SonicMQ on topic P.LHR.DMZ.FLTPUB

Details and Configuration
---------------------------

Messages are written off this topic and are published to a local WebLogic topic. The local Topic and ConnectionFactory need 
to be configured in the WebLogic domain. The names of the ConnectionFactory and Topic are passed to the MDB using environment
variables in the ejb-jar.xml

            <env-entry>
                <env-entry-name>WLFlightPubTCF</env-entry-name>
                <env-entry-type>java.lang.String</env-entry-type>
                <env-entry-value>WLFlightPubTCF</env-entry-value>
            </env-entry>
            <env-entry>
                <env-entry-name>WLFlightPubTopic</env-entry-name>
                <env-entry-type>java.lang.String</env-entry-type>
                <env-entry-value>WLFlightPubTopic</env-entry-value>
            </env-entry>

In this case the strings WLFlightPubTCF and WLFlightPubTopic (env-entry-name) are used by the MDB to lookup the JNDI names of 
the ConnectionFactory and Topic. These values should not be altered (if they are the following lines in the code also need to be 
altered).

topicName = (String)ctx.lookup("java:comp/env/WLFlightPubTopic");
tcfName = (String)ctx.lookup("java:comp/env/WLFlightPubTCF");

The env-entry-value strings represent the JNDI names of the ConnectionFactory and Topic. These can be changed to match the JMS 
resources configured in WebLogic.


The MDB uses activation properties to configure its connection to the SonicMQ. The values that control these are:

                <activation-config-property>
                    <activation-config-property-name>connectionURLs</activation-config-property-name>
                    <activation-config-property-value>ssl://10.55.6.62:2507,ssl://10.55.6.62:2707</activation-config-property-value>
                </activation-config-property>
                <activation-config-property>
                    <activation-config-property-name>username</activation-config-property-name>
                    <activation-config-property-value>INT-DNGESBTESTUSER</activation-config-property-value>
                </activation-config-property>
                <activation-config-property>
                    <activation-config-property-name>password</activation-config-property-name>
                    <activation-config-property-value>testuser1</activation-config-property-value>
                </activation-config-property>

As the MDB is progressed through the environments, the connection url, username and password will need to be changed. Once changed 
the MDB can be built using maven.

Other values to be mindful of are.

               </activation-config-property>
                <activation-config-property>
                    <activation-config-property-name>connectID</activation-config-property-name>
                    <activation-config-property-value>ODI_FLTPUB</activation-config-property-value>
                </activation-config-property>
                <activation-config-property>
                    <activation-config-property-name>clientID</activation-config-property-name>
                    <activation-config-property-value>ODI_FLTPUB</activation-config-property-value>
                </activation-config-property>
                <activation-config-property>
                    <activation-config-property-name>subscriptionName</activation-config-property-name>
                    <activation-config-property-value>ODI_FLTPUB_SUB</activation-config-property-value>
                </activation-config-property>
                
These should be unique for each connection i.e. if UAT + Pre-prod connect to the same ESB, the values should uniquely identify a
connection.

Other useful activation config properties is debugLevel. Setting this to 1 switches on debugging. Setting to 0 switches it off. In
a production environment debugging should not be used as it writes every message to the log file. 


Building
----------

If changed the MDB needs to be re-built. This is done using apache maven. Once installed on the development machine, running the
command "mvn -e clean install" will build the MDB. The deployable artefact can then be found in the target directory and is 
called odi-fltpub-sonic-mdb-1.0.0.jar.

Logging
---------

The MDB uses Log4j logging. It is the logging mechanism used by WebLogic. And so all log messages will be directed to the WebLogic
server logs. All log messages are set to DEBUG level. To see these logging messages, the log level of the WebLogic server needs to
be switched to DEBUG.