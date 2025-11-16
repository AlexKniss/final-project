package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for BitField class
 * Tests all public methods with various scenarios including edge cases
 */
public class BitFieldTest {
    
    private BitField singleBitField;
    private BitField multiBitField;
    private BitField nibbleField;
    private BitField byteField;
    private BitField zeroMaskField;
    
    @BeforeEach
    void setUp() {
        singleBitField = new BitField(0x01);      // Single bit at position 0
        multiBitField = new BitField(0x0E);       // Bits 1, 2, 3 (mask: 00001110)
        nibbleField = new BitField(0xF0);         // Upper nibble (mask: 11110000)
        byteField = new BitField(0xFF);           // Full byte (mask: 11111111)
        zeroMaskField = new BitField(0x00);       // Zero mask
    }
    
    @Test
    @DisplayName("Test BitField constructor with various masks")
    void testConstructor() {
        // Test that BitFields are created without exceptions
        assertDoesNotThrow(() -> new BitField(0x01));
        assertDoesNotThrow(() -> new BitField(0xFFFFFFFF));
        assertDoesNotThrow(() -> new BitField(0x00));
        assertDoesNotThrow(() -> new BitField(0x80000000));
    }
    
    @Test
    @DisplayName("Test getValue method")
    void testGetValue() {
        // Single bit field tests
        assertEquals(1, singleBitField.getValue(0x01));
        assertEquals(0, singleBitField.getValue(0x00));
        assertEquals(0, singleBitField.getValue(0x02));
        
        // Multi-bit field tests (bits 1,2,3 with shift count 1)
        assertEquals(0, multiBitField.getValue(0x00));  // 0000 -> 0
        assertEquals(1, multiBitField.getValue(0x02));  // 0010 -> 1
        assertEquals(2, multiBitField.getValue(0x04));  // 0100 -> 2
        assertEquals(7, multiBitField.getValue(0x0E));  // 1110 -> 7
        assertEquals(0, multiBitField.getValue(0x01));  // 0001 -> 0 (bit not in mask)
        
        // Nibble field tests (upper 4 bits with shift count 4)
        assertEquals(0, nibbleField.getValue(0x0F));    // 00001111 -> 0
        assertEquals(1, nibbleField.getValue(0x10));    // 00010000 -> 1
        assertEquals(15, nibbleField.getValue(0xF0));   // 11110000 -> 15
        
        // Byte field tests
        assertEquals(255, byteField.getValue(0xFF));
        assertEquals(128, byteField.getValue(0x80));
        assertEquals(0, byteField.getValue(0x00));
    }
    
    @Test
    @DisplayName("Test getShortValue method")
    void testGetShortValue() {
        assertEquals((short)1, singleBitField.getShortValue((short)0x01));
        assertEquals((short)0, singleBitField.getShortValue((short)0x00));
        assertEquals((short)7, multiBitField.getShortValue((short)0x0E));
        assertEquals((short)15, nibbleField.getShortValue((short)0xF0));
    }
    
    @Test
    @DisplayName("Test getRawValue method")
    void testGetRawValue() {
        // Single bit field
        assertEquals(0x01, singleBitField.getRawValue(0x01));
        assertEquals(0x00, singleBitField.getRawValue(0x00));
        assertEquals(0x00, singleBitField.getRawValue(0x02));
        
        // Multi-bit field
        assertEquals(0x0E, multiBitField.getRawValue(0x0E));
        assertEquals(0x02, multiBitField.getRawValue(0x02));
        assertEquals(0x00, multiBitField.getRawValue(0x01));
        
        // Nibble field
        assertEquals(0xF0, nibbleField.getRawValue(0xF0));
        assertEquals(0x00, nibbleField.getRawValue(0x0F));
    }
    
    @Test
    @DisplayName("Test getShortRawValue method")
    void testGetShortRawValue() {
        assertEquals((short)0x01, singleBitField.getShortRawValue((short)0x01));
        assertEquals((short)0x0E, multiBitField.getShortRawValue((short)0x0E));
        assertEquals((short)0xF0, nibbleField.getShortRawValue((short)0xF0));
    }
    
    @Test
    @DisplayName("Test isSet method")
    void testIsSet() {
        // Single bit field
        assertTrue(singleBitField.isSet(0x01));
        assertFalse(singleBitField.isSet(0x00));
        assertFalse(singleBitField.isSet(0x02));
        
        // Multi-bit field
        assertTrue(multiBitField.isSet(0x02));  // Any bit set
        assertTrue(multiBitField.isSet(0x04));
        assertTrue(multiBitField.isSet(0x0E));  // All bits set
        assertFalse(multiBitField.isSet(0x01)); // Bit not in mask
        assertFalse(multiBitField.isSet(0x00)); // No bits set
        
        // Zero mask field
        assertFalse(zeroMaskField.isSet(0xFFFFFFFF));
    }
    
    @Test
    @DisplayName("Test isAllSet method")
    void testIsAllSet() {
        // Single bit field
        assertTrue(singleBitField.isAllSet(0x01));
        assertFalse(singleBitField.isAllSet(0x00));
        assertTrue(singleBitField.isAllSet(0xFF));  // Other bits don't matter
        
        // Multi-bit field
        assertTrue(multiBitField.isAllSet(0x0E));   // All bits set
        assertFalse(multiBitField.isAllSet(0x02));  // Only one bit set
        assertFalse(multiBitField.isAllSet(0x06));  // Two bits set
        assertTrue(multiBitField.isAllSet(0xFE));   // All bits set + others
        
        // Zero mask field
        assertTrue(zeroMaskField.isAllSet(0x00));
        assertTrue(zeroMaskField.isAllSet(0xFFFFFFFF));
    }
    
    @Test
    @DisplayName("Test setValue method")
    void testSetValue() {
        // Single bit field
        assertEquals(0x01, singleBitField.setValue(0x00, 1));
        assertEquals(0x00, singleBitField.setValue(0x01, 0));
        assertEquals(0x03, singleBitField.setValue(0x02, 1));  // Set bit 0, preserve bit 1
        
        // Multi-bit field (shift count = 1)
        assertEquals(0x02, multiBitField.setValue(0x00, 1));  // 1 << 1 = 0x02
        assertEquals(0x04, multiBitField.setValue(0x00, 2));  // 2 << 1 = 0x04
        assertEquals(0x0E, multiBitField.setValue(0x00, 7));  // 7 << 1 = 0x0E
        assertEquals(0x01, multiBitField.setValue(0x0F, 0));  // Clear bits, preserve others
        
        // Nibble field (shift count = 4)
        assertEquals(0x10, nibbleField.setValue(0x00, 1));    // 1 << 4 = 0x10
        assertEquals(0xF0, nibbleField.setValue(0x00, 15));   // 15 << 4 = 0xF0
        assertEquals(0x0F, nibbleField.setValue(0xFF, 0));    // Clear upper nibble
    }
    
    @Test
    @DisplayName("Test setShortValue method")
    void testSetShortValue() {
        assertEquals((short)0x01, singleBitField.setShortValue((short)0x00, (short)1));
        assertEquals((short)0x0E, multiBitField.setShortValue((short)0x00, (short)7));
        assertEquals((short)0xF0, nibbleField.setShortValue((short)0x00, (short)15));
    }
    
    @Test
    @DisplayName("Test clear method")
    void testClear() {
        // Single bit field
        assertEquals(0x00, singleBitField.clear(0x01));
        assertEquals(0x02, singleBitField.clear(0x03));  // Clear bit 0, preserve bit 1
        
        // Multi-bit field
        assertEquals(0x01, multiBitField.clear(0x0F));   // Clear bits 1,2,3, preserve bit 0
        assertEquals(0x00, multiBitField.clear(0x0E));
        
        // Nibble field
        assertEquals(0x0F, nibbleField.clear(0xFF));     // Clear upper nibble, preserve lower
        assertEquals(0x00, nibbleField.clear(0xF0));
    }
    
    @Test
    @DisplayName("Test clearShort method")
    void testClearShort() {
        assertEquals((short)0x00, singleBitField.clearShort((short)0x01));
        assertEquals((short)0x01, multiBitField.clearShort((short)0x0F));
        assertEquals((short)0x0F, nibbleField.clearShort((short)0xFF));
    }
    
    @Test
    @DisplayName("Test clearByte method")
    void testClearByte() {
        assertEquals((byte)0x00, singleBitField.clearByte((byte)0x01));
        assertEquals((byte)0x01, multiBitField.clearByte((byte)0x0F));
        assertEquals((byte)0x0F, nibbleField.clearByte((byte)0xFF));
    }
    
    @Test
    @DisplayName("Test set method")
    void testSet() {
        // Single bit field
        assertEquals(0x01, singleBitField.set(0x00));
        assertEquals(0x03, singleBitField.set(0x02));    // Set bit 0, preserve bit 1
        
        // Multi-bit field
        assertEquals(0x0F, multiBitField.set(0x01));     // Set bits 1,2,3, preserve bit 0
        assertEquals(0x0E, multiBitField.set(0x00));
        
        // Nibble field
        assertEquals(0xFF, nibbleField.set(0x0F));       // Set upper nibble, preserve lower
        assertEquals(0xF0, nibbleField.set(0x00));
    }
    
    @Test
    @DisplayName("Test setShort method")
    void testSetShort() {
        assertEquals((short)0x01, singleBitField.setShort((short)0x00));
        assertEquals((short)0x0F, multiBitField.setShort((short)0x01));
        assertEquals((short)0xFF, nibbleField.setShort((short)0x0F));
    }
    
    @Test
    @DisplayName("Test setByte method")
    void testSetByte() {
        assertEquals((byte)0x01, singleBitField.setByte((byte)0x00));
        assertEquals((byte)0x0F, multiBitField.setByte((byte)0x01));
        assertEquals((byte)0xFF, nibbleField.setByte((byte)0x0F));
    }
    
    @Test
    @DisplayName("Test setBoolean method")
    void testSetBoolean() {
        // Single bit field
        assertEquals(0x01, singleBitField.setBoolean(0x00, true));
        assertEquals(0x00, singleBitField.setBoolean(0x01, false));
        assertEquals(0x03, singleBitField.setBoolean(0x02, true));   // Set bit 0, preserve bit 1
        assertEquals(0x02, singleBitField.setBoolean(0x03, false));  // Clear bit, preserve others
        
        // Multi-bit field
        assertEquals(0x0E, multiBitField.setBoolean(0x00, true));
        assertEquals(0x01, multiBitField.setBoolean(0x0F, false));
    }
    
    @Test
    @DisplayName("Test setShortBoolean method")
    void testSetShortBoolean() {
        assertEquals((short)0x01, singleBitField.setShortBoolean((short)0x00, true));
        assertEquals((short)0x00, singleBitField.setShortBoolean((short)0x01, false));
        assertEquals((short)0x0E, multiBitField.setShortBoolean((short)0x00, true));
        assertEquals((short)0x01, multiBitField.setShortBoolean((short)0x0F, false));
    }
    
    @Test
    @DisplayName("Test setByteBoolean method")
    void testSetByteBoolean() {
        assertEquals((byte)0x01, singleBitField.setByteBoolean((byte)0x00, true));
        assertEquals((byte)0x00, singleBitField.setByteBoolean((byte)0x01, false));
        assertEquals((byte)0x0E, multiBitField.setByteBoolean((byte)0x00, true));
        assertEquals((byte)0x01, multiBitField.setByteBoolean((byte)0x0F, false));
    }
    
    @Test
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCases() {
        // Test with maximum integer values
        BitField maxField = new BitField(0xFFFFFFFF);
        assertEquals(0xFFFFFFFF, maxField.getRawValue(0xFFFFFFFF));
        assertEquals(0xFFFFFFFF, maxField.getValue(0xFFFFFFFF));
        
        // Test with high bit set
        BitField highBitField = new BitField(0x80000000);
        assertTrue(highBitField.isSet(0x80000000));
        assertFalse(highBitField.isSet(0x7FFFFFFF));
        
        // Test zero mask behavior
        assertEquals(0, zeroMaskField.getValue(0xFFFFFFFF));
        assertEquals(0, zeroMaskField.getRawValue(0xFFFFFFFF));
        assertFalse(zeroMaskField.isSet(0xFFFFFFFF));
        assertTrue(zeroMaskField.isAllSet(0xFFFFFFFF));
    }
    
    @Test
    @DisplayName("Test complex bit patterns")
    void testComplexBitPatterns() {
        // Test alternating bits pattern
        BitField alternatingField = new BitField(0xAAAAAAAA);
        assertTrue(alternatingField.isSet(0xAAAAAAAA));
        assertFalse(alternatingField.isSet(0x55555555));
        
        // Test single bit in middle
        BitField middleBitField = new BitField(0x00008000);
        assertEquals(1, middleBitField.getValue(0x00008000));
        assertEquals(0, middleBitField.getValue(0x00007000));
    }
}