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

## License

MIT

