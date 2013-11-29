# Transponder

Transponder is a simple component which subscribes to a broadcast address to which it posts status updates about the
local conjoiner application ecosystem. It should publish occational updates as-well as respond to specific enquiries
and general enquiries from other conjoiner nodes. Status updates should include:
1. version info of all modules

Potentially, transponder could publish messages to localhost.transponder and allow locally deployed
veriticals to respond with their respective status updates and forward them along to the BCAST address.
It is possible to have each vertical take care of its own status updates, solving the configuration however this requires per module configuration
as to which topics. Its perhaps easier to define a `status` responder within each


