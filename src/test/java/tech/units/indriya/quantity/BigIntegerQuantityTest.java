/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tech.units.indriya.quantity;

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

public class BigIntegerQuantityTest {

  private final BigIntegerQuantity<ElectricResistance> ONE_OHM = new BigIntegerQuantity<ElectricResistance>(1L, Units.OHM);

  @Test
  public void divideTest() {
    BigIntegerQuantity<ElectricResistance> quantity1 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> quantity2 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.divide(quantity2);
    assertEquals(BigInteger.ONE, result.getValue());
  }

  @Test
  public void multiplyQuantityTest() {
    final BigIntegerQuantity<ElectricResistance> quantity1 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> quantity2 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.multiply(quantity2);
    assertEquals(BigInteger.valueOf(6), result.getValue());
  }

  @Test
  public void longValueTest() {
    final BigIntegerQuantity<Time> day = new BigIntegerQuantity<Time>(Long.valueOf(3), Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void doubleValueTest() {
    BigIntegerQuantity<Time> day = new BigIntegerQuantity<Time>(Long.valueOf(3), Units.DAY);
    double hours = day.doubleValue(Units.HOUR);
    assertEquals(72D, hours);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Long(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Long(10), Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void additionMustProduceCorrectResultIfSameUnits() {
    BigIntegerQuantity<ElectricResistance> twoOhm = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).longValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> expected = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).longValue(), Units.OHM);
    Quantity<ElectricResistance> actual = ONE_OHM.add(twoOhm);
    assertEquals(expected, actual);
  }

  @Test
  public void additionMustConvertToLowestPrefix() {
    for (MetricPrefix prefix : MetricPrefix.values()) {
      final String msg = String.format("testing 1 Ω + 1 %sΩ", prefix.getSymbol());
      final BigIntegerQuantity<ElectricResistance> operand = new BigIntegerQuantity<ElectricResistance>(1L, Units.OHM.prefix(prefix));
      final BigIntegerQuantity<ElectricResistance> expected;
      if (prefix.getExponent() > 0) {
        expected = new BigIntegerQuantity<ElectricResistance>(BigInteger.TEN.pow(prefix.getExponent()).add(BigInteger.ONE), Units.OHM);
      } else {
        expected = new BigIntegerQuantity<ElectricResistance>(BigInteger.TEN.pow(-prefix.getExponent()).add(BigInteger.ONE),
            Units.OHM.prefix(prefix));
      }
      assertEquals(expected, ONE_OHM.add(operand), msg);
      assertEquals(expected, operand.add(ONE_OHM), msg);
    }
  }

  @Test
  @DisplayName("1 Ω - 1001 mΩ should be -1 mΩ")
  public void subtractionMustProduceCorrectResult() {
    final BigIntegerQuantity<ElectricResistance> operand = new BigIntegerQuantity<ElectricResistance>(1001L, MILLI(Units.OHM));
    final BigIntegerQuantity<ElectricResistance> expected = new BigIntegerQuantity<ElectricResistance>(-1L, MILLI(Units.OHM));

    assertEquals(expected, ONE_OHM.subtract(operand));
    assertEquals(expected, operand.subtract(ONE_OHM).multiply(-1));
  }
}
