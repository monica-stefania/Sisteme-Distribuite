#!/bin/bash

#java -jar out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar &


for i in {1..20}
do
	echo "Starting $i bid"
	java -jar out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar &
	pids[${i}]=$!
done

# wait for all pids
echo "Now we wait"
for pid in ${pids[*]}; do
    wait $pid
done
