
var etClientName = context.getVariable("request.header.et-client-name");

var identifiedUser = {
    quota: "3000", spikeArrest: "300ps"
};

var unidentifiedUser = {
    quota: "30", spikeArrest: "2ps"
};

if (etClientName) { // if client identified
    quotaAllowed = identifiedUser.quota;
    spikeArrestAllowed = identifiedUser.spikeArrest;
} else { // if not identified -> rate on ip
    etClientName = "unkown-" + context.getVariable("client.ip")
    quotaAllowed = unidentifiedUser.quota;
    spikeArrestAllowed = unidentifiedUser.spikeArrest;
    // Add et-client-name with ip
    context.setVariable("request.header.Et-Client-Name", etClientName);
}

context.setVariable("quota.client.identifier", etClientName);
context.setVariable("spikeArrest.client.identifier", etClientName);

// et quota and spike limits
context.setVariable("quota.client.allowed", quotaAllowed);
context.setVariable("spikeArrest.client.allowed", spikeArrestAllowed);