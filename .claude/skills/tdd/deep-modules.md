# Módulos Profundos

De "A Philosophy of Software Design":

**Módulo profundo** = interface pequena + muita implementação

```
┌─────────────────────┐
│  Interface Pequena  │  ← Poucos métodos, parâmetros simples
├─────────────────────┤
│                     │
│                     │
│ Implementação       │  ← Lógica complexa escondida
│ Profunda            │
│                     │
└─────────────────────┘
```

**Módulo raso** = interface grande + pouca implementação (evitar)

```
┌─────────────────────────────────┐
│       Interface Grande          │  ← Muitos métodos, parâmetros complexos
├─────────────────────────────────┤
│  Implementação Fina             │  ← Apenas repassa
└─────────────────────────────────┘
```

Ao projetar interfaces, pergunte:

- Posso reduzir o número de métodos?
- Posso simplificar os parâmetros?
- Posso esconder mais complexidade internamente?
