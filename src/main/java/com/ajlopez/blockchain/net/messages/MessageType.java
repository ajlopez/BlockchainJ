package com.ajlopez.blockchain.net.messages;

/**
 * Created by ajlopez on 19/01/2018.
 */
public enum MessageType {
    STATUS,
    TRANSACTION,
    BLOCK,
    GET_BLOCK_BY_HASH,
    GET_BLOCK_BY_NUMBER,
    TRIE_NODE,
    GET_TRIE_NODE,
    GET_BLOCK_HASHES,
    GET_STATUS
}
