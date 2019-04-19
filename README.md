# BlockChainJ

Simple blockchain implementation, in Java, WIP

## Description

This blockchain implementation has:

- Block: with number, parent block and list of transactions
- Transaction: send value from sender to receiver account
- Account: with balance and optional smart contract code
- Virtual Machine: to execute smart contracts

The world state keeps the account states. Each account has a balance and smart contract storage.

## References

- [SpongyCastle](https://rtyley.github.io/spongycastle)
- [BouncyCastle Java](http://www.bouncycastle.org/java.html)
- [RLP](https://github.com/ethereum/wiki/wiki/RLP)
- [RLP Tests](https://github.com/ethereum/tests/blob/develop/RLPTests/rlptest.json)
- [How to Build Your Own Blockchain Part 3 — Writing Nodes that Mine and Talk](https://bigishdata.com/2017/11/02/build-your-own-blockchain-part-3-writing-nodes-that-mine/)
- [Create a simple HTTP Web Server in Java](https://medium.com/@ssaurel/create-a-simple-http-web-server-in-java-3fc12b29d5fd)
- [Function Pointers in Java](https://programming.guide/java/function-pointers-in-java.html)
- [A Simple Java TCP Server and TCP Client](https://systembash.com/a-simple-java-tcp-server-and-tcp-client/)
- [BlockBench: A Framework for Analyzing Private Blockchains](https://github.com/ooibc88/blockbench)
- [Pantheon: An enterprise-grade Java-based, Apache 2.0 licensed Ethereum client](https://github.com/PegaSysEng/pantheon)
- [Add timer metrics for EVM operations #551](https://github.com/PegaSysEng/pantheon/pull/551)
- [Do a Simple HTTP Request in Java](https://www.baeldung.com/java-http-request)
- [Model 0: low-level defence measures for Sybil attacks in P2P networks](https://blog.golemproject.net/model-0-low-level-defence-measures-for-sybil-attacks-in-p2p-networks/)

## To Do

- Coin type
- Signed transactions
- DataWord or Coin in Transaction value, instead of BigInteger
- DataWord or Coin in Account balance, instead of BigInteger
- Block JSON serialization
- Block transaction hash should be the hash of a trie
- Difficulty in block
- Total difficulty in block JSON serialization
- Numeric values in hexadecimal in JSON serialization
- Transaction receipts
- Log emit
- Create contracts
- Call contracts in Virtual Machine

## License

MIT

