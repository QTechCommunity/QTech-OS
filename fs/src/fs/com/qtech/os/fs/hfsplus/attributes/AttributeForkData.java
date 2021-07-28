package com.qtech.os.fs.hfsplus.attributes;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.qtech.os.fs.hfsplus.HfsPlusFileSystem;
import com.qtech.os.fs.hfsplus.HfsPlusForkData;
import com.qtech.os.fs.hfsplus.catalog.CatalogNodeId;
import com.qtech.os.util.BigEndian;

/**
 * Attribute data stored in the resource fork for the file ('HFSPlusAttrForkData').
 *
 * @author Luke Quinane
 */
public class AttributeForkData extends AttributeData {

    /**
     * The fork data containing the attribute data.
     */
    private HfsPlusForkData fork;

    /**
     * Reads in attribute fork data.
     *
     * @param cnid the associated node ID.
     * @param source the source buffer to read from.
     * @param offset the offset to read from.
     */
    public AttributeForkData(CatalogNodeId cnid, byte[] source, int offset) {
        recordType = BigEndian.getUInt32(source, offset);
        fork = new HfsPlusForkData(cnid, false, source, offset);
    }

    @Override
    public long getSize() {
        return fork.getTotalSize();
    }

    @Override
    public void read(HfsPlusFileSystem fs, long fileOffset, ByteBuffer dest) throws IOException {
        fork.read(fs, fileOffset, dest);
    }

    @Override
    public String toString() {
        return String.format("fork-attribute:[%s]", fork);
    }
}
