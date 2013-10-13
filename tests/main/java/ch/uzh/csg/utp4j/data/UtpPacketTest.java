package ch.uzh.csg.utp4j.data;

import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.MAX_UBYTE;
import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.MAX_UINT;
import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.MAX_USHORT;
import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.longToUbyte;
import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.longToUint;
import static ch.uzh.csg.utp4j.data.bytes.UnsignedTypesUtil.longToUshort;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtpPacketTest {

	@Test
	public void testHeaderNoExtensionToByteArray() {
		
		UtpPacket header = createMaxHeader(UtpPacketUtils.DATA, UtpPacketUtils.NO_EXTENSION);
		byte[] array = header.toByteArray();
		
		assertEquals(UtpPacketUtils.DATA, array[0]);
		assertEquals(UtpPacketUtils.NO_EXTENSION, array[1]);

		for (int i = 2; i < array.length; i++) {
			assertEquals((byte) 0xFF, array[i]);
		}
		
		assertEquals(UtpPacketUtils.DEF_HEADER_LENGTH, header.getPacketLength());
		assertEquals(UtpPacketUtils.DEF_HEADER_LENGTH, array.length);
		
	}
	
	@Test
	public void testHeaderSelectiveAckToByteArray() {
		
		UtpPacket header = createMaxHeader(UtpPacketUtils.DATA, UtpPacketUtils.SELECTIVE_ACK);
		
		int extensionLength = applySelectiveAck(header, UtpPacketUtils.NO_EXTENSION);
		
		byte[] actual = header.toByteArray();
		
		assertEquals(UtpPacketUtils.DATA, actual[0]);
		assertEquals(UtpPacketUtils.SELECTIVE_ACK, actual[1]);

		// EXTENSIONLESS HEADER
		for (int i = 2; i < 20; i++) {
			assertEquals((byte) 0xFF, actual[i]);
		}
		
		// BYTE INDICATING NEXT EXTENSION
		assertEquals(UtpPacketUtils.NO_EXTENSION, actual[20]);
		
		// BYTE INDICATING LENGTH OF SELECTIVE ACK PAYLOAD IN BYTES
		assertEquals(longToUbyte(6), actual[21]);
		
		// BITMASK OF SELECTIVE ACK
		for (int i = 22; i < actual.length; i++) {
			assertEquals((byte) 0xFF, actual[i]);
		}
		
		assertEquals(UtpPacketUtils.DEF_HEADER_LENGTH + extensionLength, header.getPacketLength());
		assertEquals(UtpPacketUtils.DEF_HEADER_LENGTH + extensionLength, actual.length);
		

	}
	
	@Test
	public void testFromByteArray() {
		UtpPacket pkt = createMaxHeader(UtpPacketUtils.DATA, UtpPacketUtils.NO_EXTENSION);
		UtpPacket fromArrayPkt = new UtpPacket();
		fromArrayPkt.setFromByteArray(pkt.toByteArray(), 20, 0);
		
		assertEquals(pkt, fromArrayPkt);
	}

	/**
	 *  Attaching an selective ack to the header with a length of 6 bytes, each's value is 0xFF. 
	 * @param header
	 * @param nextextension 
	 * @return legnth of extension
	 */
	private int applySelectiveAck(UtpPacket header, byte nextextension) {
		
		SelectiveAckHeaderExtension selectiveAck = new SelectiveAckHeaderExtension();
		selectiveAck.setNextExtension(nextextension);
		
		byte[] bitMask = { longToUbyte(MAX_UBYTE), longToUbyte(MAX_UBYTE),
						   longToUbyte(MAX_UBYTE), longToUbyte(MAX_UBYTE), 
						   longToUbyte(MAX_UBYTE), longToUbyte(MAX_UBYTE) };
		
		selectiveAck.setBitMask(bitMask);
		
		UtpHeaderExtension[] extensions = { selectiveAck };
		header.setExtensions(extensions);
		
		return 8;
	}
	
	
	/**
	 * returns standart header with full of 0xFF fields except of first two bytes which are specified as parameters.
	 * @param typeVersion
	 * @param firstExtension
	 * @return UTPHeader
	 */
	private UtpPacket createMaxHeader(byte typeVersion, byte firstExtension) {
		UtpPacket header = new UtpPacket();
		header.setAckNumber(longToUshort(MAX_USHORT));
		header.setFirstExtension(firstExtension);
		header.setConnectionId(longToUshort(MAX_USHORT));
		header.setSequenceNumber(longToUshort(MAX_USHORT));
		header.setTimestamp(longToUint(MAX_UINT));
		header.setTimestampDifference(longToUint(MAX_UINT));
		header.setTypeVersion(typeVersion);
		header.setWindowSize(longToUint(MAX_UINT));
		return header;
		
	}

}
