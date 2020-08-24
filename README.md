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
- [Introducing Hobbits: A lightweight wire protocol for ETH 2.0](https://medium.com/whiteblock/introducing-hobbits-a-lightweight-wire-protocol-for-eth-2-0-b1bfae5e4843)
- [Ethereum 2.0’s Nodes Need to Talk – A Solution Is ‘Hobbits’](https://www.coindesk.com/testing-ethereum-2-0-requires-basic-signaling-a-solution-is-hobbits)
- [Wisps: The Magical World of Create2](https://blog.ricmoo.com/wisps-the-magical-world-of-create2-5c2177027604)
- [Quantifying Immutability](https://medium.com/ethereum-classic/quantifying-immutability-e8f2b1bb9301)
- [Empirically Analyzing Ethereum’s Gas Mechanism](https://arxiv.org/pdf/1905.00553.pdf)
- [Blockchain Scalability: Do Layer I Solutions Hold the Key?](https://hackernoon.com/blockchain-scalability-do-layer-i-solutions-hold-the-key-f3d9388c60f3)
- [Overview of Layer 2 approaches: Plasma, State Channels, Side Chains, Roll Ups](https://nearprotocol.com/blog/layer-2/)
- [Blockchain Consensus: The Past, Present, and Future](https://hackernoon.com/blockchain-consensus-the-past-present-and-future-112cd1a4189a)
- [Warp Sync](https://wiki.parity.io/Warp-Sync)
- [What is a light client and why you should care?](https://www.parity.io/what-is-a-light-client/)
- [Transactions Per Second in Permissioned Blockchains — Does It Even Matter?](https://hackernoon.com/transactions-per-second-in-private-blockchains-does-it-even-matter-bf67dec56b76)
- [Vac modular peer-to-peer messaging stack](https://vac.dev/vac-overview)
- [Pantheon Enterprise Ethereum Client](https://docs.pantheon.pegasys.tech/en/latest/)
- [What Comprises an Ethereum Fullnode Implementation?](https://medium.com/blockchannel/what-comprises-an-ethereum-fullnode-implementation-a1e72f213ca6)
- [Announcing Hyperledger Besu](https://www.hyperledger.org/blog/2019/08/29/announcing-hyperledger-besu)
- [Fast Ethereum Virtual Machine implementation](https://github.com/ethereum/evmone)
- [Synchrony, Asynchrony and Partial synchrony](https://ittaiab.github.io/2019-06-01-2019-5-31-models/)
- [How to implement a most-recently-used cache](https://stackoverflow.com/questions/583852/how-to-implement-a-most-recently-used-cache)
- [EIP 2124, Fork identifier for chain compatibility checks](https://twitter.com/trent_vanepps/status/1184677267995320322)
- [Special-purpose light clients for old receipts and transactions](https://ethereum-magicians.org/t/special-purpose-light-clients-for-old-receipts-and-transactions/3711)
- [A Primer on Ethereum Blockchain Light Clients](https://medium.com/@rauljordan/a-primer-on-ethereum-blockchain-light-clients-f3cadde49137)
- [Byzantine Failure — Why Blockchain Development is Difficult](https://medium.com/codechain/byzantine-failure-why-blockchain-development-is-difficult-1d2da8de9f03)
- [How does Ethereum work, anyway?](https://medium.com/@preethikasireddy/how-does-ethereum-work-anyway-22d1df506369)
- [Understanding Ethereum’s P2P Network](https://medium.com/shyft-network-media/understanding-ethereums-p2p-network-86eeaa3345)
- [Guidelines for low-level cryptography software](https://github.com/veorq/cryptocoding)
- [Breaking down the differences between PoW and PoS](https://medium.com/@stakingrewards/research-report-is-proof-of-stake-better-than-proof-of-work-222d048ccef5)
- [Mastering The Fundamentals of Ethereum (For New Blockchain Devs) Part III — Wallets, Keys, And Accounts](https://medium.com/@markmuskardin/mastering-the-fundamentals-of-ethereum-for-new-blockchain-devs-part-iii-wallets-keys-and-4cd3175b535b)
- [Byzantine Fault Tolerance in a nutshell](https://medium.com/coinmonks/byzantine-fault-tolerance-in-a-nutshell-bc7762ffb996)
- [The 1.x Files: The State of Stateless Ethereum](https://blog.ethereum.org/2019/12/30/eth1x-files-state-of-stateless-ethereum/)
- [Stateless Ethereum: Binary Tries Experiment](https://medium.com/@mandrigin/stateless-ethereum-binary-tries-experiment-b2c035497768)
- [Protocol changes to bound witness size](https://ethereum-magicians.org/t/protocol-changes-to-bound-witness-size/3885)
- [Developing the EigenTrust Algorithm and Determining Authenticity Online](https://medium.com/oscar-tech/developing-the-eigentrust-algorithm-and-determining-trustworthiness-online-6c51b2c2938f)
- [The EigenTrust Algorithm for Reputation Management in P2P Networks](https://nlp.stanford.edu/pubs/eigentrust.pdf)
- [State Provider Models in Ethereum 2.0](https://ethresear.ch/t/state-provider-models-in-ethereum-2-0/6750)
- [With fraud-proof-free data availability proofs, we can have scalable data chains without committees](https://ethresear.ch/t/with-fraud-proof-free-data-availability-proofs-we-can-have-scalable-data-chains-without-committees/6725)
- [Requirements for Ethereum Private Sidechains](https://arxiv.org/pdf/1806.09834.pdf)
- [Ren Project](https://github.com/renproject/ren/wiki/Introduction)
- [Interledger: How to Interconnect All Blockchains and Value Networks](https://medium.com/xpring/interledger-how-to-interconnect-all-blockchains-and-value-networks-74f432e64543)
- [Transaction Announcements and Retrievals](https://github.com/ethereum/EIPs/issues/2465)
- [A-to-Z of Blockchain Consensus](https://medium.com/tendermint/a-to-z-of-blockchain-consensus-81e2406af5a3)
- [Filecoin Specification](https://filecoin-project.github.io/specs/)
- [Archive Node](https://infura.io/docs/ethereum/add-ons/archiveData)
- [Understanding Merkle pollards](https://medium.com/@jgm.orinoco/understanding-merkle-pollards-1547fc7efaa)
- [Kadcast: A Structured Approach to Broadcast in Blockchain Networks](https://eprint.iacr.org/2019/876.pdf)
- [Semi-Stateless Initial Sync Experiment](https://medium.com/@mandrigin/semi-stateless-initial-sync-experiment-897cc9c330cb)
- [Ethereum does it too! A deep dive into DHT](https://medium.com/unitychain/intro-to-dht-e98425fc05f1)
- [DHT: Attacks and Defenses](https://medium.com/unitychain/dht-attacks-and-defenses-e159b3d1bcf8)
- [Private Ethereum by Example](https://medium.com/coinmonks/private-ethereum-by-example-b77063bb634f)
- [What is Ethereum’s Uncle Rate and Why Does It Matter?](https://ethgasstation.info/blog/ethereum-uncle-rate/)
- [Ethereum Miner Test — Results](https://medium.com/bloxroute/ethereum-miner-test-results-8fbee68b7088)
- [Uncle Mining, an Ethereum Consensus Protocol Flaw](https://bitslog.com/2016/04/28/uncle-mining-an-ethereum-consensus-protocol-flaw/)
- [Does anyone understand the behavior of abi.encodePacked()?](https://www.reddit.com/r/ethdev/comments/a9i594/does_anyone_understand_the_behavior_of/)
- [“Merry Go Round” sync](https://ethresear.ch/t/merry-go-round-sync/7158)
- [Understanding Merkle pollards](https://medium.com/@jgm.orinoco/understanding-merkle-pollards-1547fc7efaa)
- [EIP 2464: eth/65: transaction announcements and retrievals](https://eips.ethereum.org/EIPS/eip-2464)
- [Survey of proposals to reduce block witness size](https://ethresear.ch/t/survey-of-proposals-to-reduce-block-witness-size/7173)
- [Understanding sparse Merkle multiproofs](https://www.wealdtech.com/articles/understanding-sparse-merkle-multiproofs/)
- [Understanding Merkle pollards](https://www.wealdtech.com/articles/understanding-merkle-pollards/)
- [Modified Merkle Patricia Trie — How Ethereum saves a state](https://medium.com/codechain/modified-merkle-patricia-trie-how-ethereum-saves-a-state-e6d7555078dd)
- [Implementing Merkle Tree and Patricia Trie](https://medium.com/coinmonks/implementing-merkle-tree-and-patricia-trie-b8badd6d9591)
- [Berty (P2P) is opening its code!](https://berty.tech/blog/open-source/)
- [Kadcast: A Structured Approach to Broadcast in Blockchain Networks](https://eprint.iacr.org/2019/876.pdf)
- [Why are transaction tries in Ethereum not plain Merkle Tries?](https://ethereum.stackexchange.com/questions/66729/why-are-transaction-tries-in-ethereum-not-plain-merkle-tries)
- [Replicate Ethereum’s mainnet in a development environment with Ganache’s forking feature](https://medium.com/@samajammin/how-to-interact-with-ethereums-mainnet-in-a-development-environment-with-ganache-3d8649df0876)
- [Awesome Chaos Engineering](https://github.com/dastergon/awesome-chaos-engineering)
- [Intro to Beam Sync](https://blog.ethereum.org/2019/12/30/eth1x-files-state-of-stateless-ethereum/)
- [An Ethereum Storage Decode tool](https://inuka.dev/an-ethereum-storage-decode-tool/)
- [EVM Bytecode Merklization](https://medium.com/ewasm/evm-bytecode-merklization-2a8366ab0c90)
- [Data from the Ethereum stateless prototype](https://medium.com/@akhounov/data-from-the-ethereum-stateless-prototype-8c69479c8abc)
- [State Rent Rough Proposal](https://github.com/ledgerwatch/eth_state/blob/58351eb8b70fa6031da1e23c1a77d982be677078/State_rent.pdf)
- [Ethereum Series — Understanding Nonce](https://medium.com/swlh/ethereum-series-understanding-nonce-3858194b39bf)
- [What are the -Xms and -Xmx parameters when starting JVM?](https://stackoverflow.com/questions/14763079/what-are-the-xms-and-xmx-parameters-when-starting-jvm)
- [Java Xmx and Various Memory Management Options in Java](https://www.udemy.com/blog/java-xmx/)
- [Account Abstraction, Stateless Mining Eth1.x/Eth 2 Implementation, Rationale Document](https://hackmd.io/y7uhNbeuSziYn1bbSXt4ww?view)
- [Testground: A platform for testing, benchmarking, and simulating distributed and p2p systems at scale](https://github.com/testground/testground)
- [UTXO vs Account/Balance Model](https://medium.com/@sunflora98/utxo-vs-account-balance-model-5e6470f4e0cf)
- [History of Ethereum Hard Forks](https://medium.com/mycrypto/the-history-of-ethereum-hard-forks-6a6dae76d56f)
- [What is a light client and why you should care?](https://www.parity.io/what-is-a-light-client/)
- [The 1.x Files: A Primer for the Witness Specification](https://blog.ethereum.org/2020/05/04/eth1x-witness-primer/)
- [The hard DiSC of the world computer](https://medium.com/ethereum-swarm/the-hard-disc-of-the-world-computer-4f3d41bf9ddb)
- [Contra-*: Mechanisms for Countering Spam Attacks on Blockchain’s Memory Pools](https://arxiv.org/pdf/2005.04842.pdf)
- [What’s Ahead for the OpenEthereum Client](https://blog.gnosis.pm/whats-ahead-for-the-openethereum-client-43da126921c2)
- [Geth Pink Marble (v1.9.14)](https://github.com/ethereum/go-ethereum/releases/tag/v1.9.14)
- [Free the Birds: the Case for Tokenized Gas](https://ethresear.ch/t/free-the-birds-the-case-for-tokenized-gas/7385)
- [Mjolnir: Tooling for BAT Apollo](https://brave.com/mjolnir-tooling-for-bat-apollo/)
- [Getting Synced - Wiki Parity Tech Documentation](https://openethereum.github.io/wiki/Getting-Synced)
- [Notes on syncing Ethereum nodes](https://medium.com/aleph-zero/notes-on-syncing-ethereum-nodes-77d4161a522e)
- [Why do we need both nonce and mixhash values in a block?](https://ethereum.stackexchange.com/questions/5833/why-do-we-need-both-nonce-and-mixhash-values-in-a-block)
- [Snap Synchronization](https://twitter.com/peter_szilagyi/status/1263668104493662210)
- [Does installing geth download the entire blockchain?](https://ethereum.stackexchange.com/questions/12023/does-installing-geth-download-the-entire-blockchain/12028)
- [Ethereum Snapshot Protocol (SNAP)](https://github.com/ethereum/devp2p/blob/3fe9713658f3b3b56e4e99493c54f313e11b43a0/caps/snap.md)
- [Gas and circuit constraint benchmarks of binary and quinary incremental Merkle trees using the Poseidon hash function](https://ethresear.ch/t/gas-and-circuit-constraint-benchmarks-of-binary-and-quinary-incremental-merkle-trees-using-the-poseidon-hash-function/7446)
- [Optimizing sparse Merkle trees](https://ethresear.ch/t/optimizing-sparse-merkle-trees/3751/2)
- [Ethereum 2 Specs: Merkle proofs](https://github.com/ethereum/eth2.0-specs/blob/ced6208d55d26d63f532d4bb031869740b2a111c/specs/light_client/merkle_proofs.md)
- [Some quick numbers on code merkelization](https://ethresear.ch/t/some-quick-numbers-on-code-merkelization/7260)
- [Open problem: improving stealth addresses Cryptography](https://ethresear.ch/t/open-problem-improving-stealth-addresses/7438)
- [Towards the first release of Turbo-geth](https://ledgerwatch.github.io/turbo_geth_release.html)
- [Analysis of EIP-1559](https://insights.deribit.com/market-research/analysis-of-eip-1559/)
- [Analysis of EIP-2593 (Escalator)](https://insights.deribit.com/market-research/analysis-of-eip-2593-escalator/)
- [Binary Trie Format](https://ethresear.ch/t/binary-trie-format/7621)
- [A World Without (block) Limits!](https://medium.com/@MicahZoltu/a-world-without-block-limits-f3ecc926cd68)
- [ReGenesis Explained](https://medium.com/@mandrigin/regenesis-explained-97540f457807)
- [Encrypted Blockchain Databases](https://eprint.iacr.org/2020/827.pdf)
- [Simpler Ethereum sync: Major/minor state snapshots, blockchain files, receipt files](https://ethresear.ch/t/simpler-ethereum-sync-major-minor-state-snapshots-blockchain-files-receipt-files/7672)
- [TurboGeth Staged Sync](https://github.com/ledgerwatch/turbo-geth/tree/master/eth/stagedsync)
- [EIP-1052: EXTCODEHASH opcode](https://eips.ethereum.org/EIPS/eip-1052)
- [What is the EXTCODEHASH?](https://soliditydeveloper.com/extcodehash)
- [Ethereum Virtual Machine Opcodes](https://ethervm.io/)
- [Ethereum Classic Simplicity](https://etherplan.com/2020/08/19/ethereum-classic-simplicity/12470/)
- [evmone is a C++ implementation of the Ethereum Virtual Machine (EVM)](https://github.com/ethereum/evmone)
- [EVM384 – Can we do Fast Crypto in EVM?](https://notes.ethereum.org/@axic/evm384)


## To Do

- Signed transactions
- Block JSON serialization
- Total difficulty in block JSON serialization
- Numeric values in hexadecimal in JSON serialization
- Transaction receipts
- Log emit
- Update transaction pool with new block information
- Shared path in trie
- Review incoming message processing (queue(s)?)
- Create/Call opcodes in virtual machine

## License

MIT

