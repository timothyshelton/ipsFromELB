/**
 * Created by tshelto17 on 11/6/15.
 */


import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions


/**
 * Simple groovy app to translate an AWS ELB name into a list of all the private IP addresses of everything
 * defined to it
 */
class ipsFromELB {

    /** @TODO: allow for InstanceCreds, too **/
    private AWSCredentials creds = new ProfileCredentialsProvider().getCredentials();

    /**
     * Takes in the name of the ELB
     *
     * @param ELB_NAME
     * @return String list of the IP addresses
     */
    def getIPs(ELB_NAME) {

        def retval = []
        try {

            /** @TODO: make this configurable **/
            Region eugene = Region.getRegion(Regions.US_WEST_2);


            AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(creds)
            elb.setRegion(eugene)

            AmazonEC2Client ec2 = new AmazonEC2Client(creds);
            ec2.setRegion(eugene)

            def oneElb = elb.describeLoadBalancers(new DescribeLoadBalancersRequest()
                    .withLoadBalancerNames(ELB_NAME))
                    .getLoadBalancerDescriptions()

            oneElb.each() { elbdesc ->

                def instances = elbdesc.getInstances()
                def instanceIdArray = new ArrayList<String>()

                //convert collection of Instance objects to collection of InstanceId Strings
                instances.each() { oneInstance ->
                    instanceIdArray << oneInstance.getInstanceId()
                }

                //figure out the port
                /** @TODO: return this, too? **/
                def port = elbdesc.getListenerDescriptions().get(0).listener.instancePort;

                //now ask for the instance descriptions
                DescribeInstancesRequest ec2_request = new DescribeInstancesRequest()
                ec2_request.setInstanceIds(instanceIdArray)
                DescribeInstancesResult iRes = ec2.describeInstances(ec2_request)

                iRes.getReservations().each() { res ->

                    res.instances.each() { oneBox ->
                        def IP = oneBox.getPrivateIpAddress()
                        retval << IP

                    }
                }
            }
        }
        catch(Exception ex) {
            System.err.println(ex.getMessage())
        }

        return retval.join("\n")

    }

    public static void main(String[] args) {

        def app = new ipsFromELB()
        System.out.print(app.getIPs(args[0]))

    }
}
