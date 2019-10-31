package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 21/01/2019.
 */
public class CodeStoreTest {
    @Test
    public void getUnknownCode() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());

        Assert.assertNull(codeStore.getCode(FactoryHelper.createRandomHash()));
    }

    @Test
    public void putAndGetCode() throws IOException {
        Hash codeHash = FactoryHelper.createRandomHash();
        byte[] code = FactoryHelper.createRandomBytes(42);

        CodeStore codeStore = new CodeStore(new HashMapStore());

        codeStore.putCode(codeHash, code);
        Assert.assertArrayEquals(code, codeStore.getCode(codeHash));
    }
}
