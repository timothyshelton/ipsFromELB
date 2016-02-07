# ipsFromELB
Returns the IP addresses of all EC2 instances behind an ELB

## Overview

This can be useful to cobble together bash scripts that need to operate on a collection of EC2 instances.

Written in groovy to leverage the AWS Java SDK, rolled into a fatJar to minimize runtime dependencies.


## Usage

Usage: `java -jar ipsFromELB-1.0.jar`

Example:

```
for node in `java -jar ipsFromELB-1.0.jar myELB`
do
  ssh $node ls /
done
```


## Prerequisites

This requires Java 8 and the AWS access/secret keys to be in the usual place (environment variables or ~/.aws/credentials)
