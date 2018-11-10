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

## License

MIT

