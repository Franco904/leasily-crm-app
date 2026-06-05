# Regra de inclusão no Shared Kernel (módulo core)

O módulo `core` aceita apenas artefatos invocados por dois ou mais contextos delimitados sem
conflito nem adaptação semântica. A alternativa — usar `core` como atalho para qualquer código
compartilhado — gradualmente erodiria as fronteiras de contexto e tornaria `core` um repositório de
código acumulado sem dono claro. Código que é conveniente para mais de um contexto mas que carrega
semântica (ou razão para mudar) de apenas um deles permanece no contexto de origem.
