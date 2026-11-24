package com.fstengineering.leasilycrm.zzCiProbe

import org.junit.jupiter.api.Test

class CiProbeTest {
  @Test
  fun `Deve falhar quando o CI roda, dado que a falha é plantada`() {
    error("falha plantada para validar o upload de relatórios")
  }
}
