# CQRS na camada de Aplicação

Use Cases de escrita e de leitura operam sobre modelos distintos; o banco de dados permanece
unificado para evitar complexidade acidental. Comandos interagem com a camada de domínio apenas
quando há invariantes a validar — nesse caso, um Command model (rascunho da entrada do usuário) é
mapeado para um Agregado antes de ser entregue ao Repository; quando não há invariantes, um Write
model vai diretamente ao Repository sem passar pelo domínio. Consultas nunca tocam a camada de
domínio — lêem da fonte da verdade via Repository e entregam Read models à UI; Read models podem ser
compostos por Use Cases de consulta quando a UI exige uma visão agregada de múltiplos Agregados
pequenos.
