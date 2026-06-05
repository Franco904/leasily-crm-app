# Bons e Maus Testes

## Bons Testes

**Estilo de integração**: Teste através de interfaces reais, não mocks de partes internas.

```typescript
// BOM: Testa comportamento observável
test("usuário pode finalizar compra com carrinho válido", async () => {
  const cart = createCart();
  cart.add(product);
  const result = await checkout(cart, paymentMethod);
  expect(result.status).toBe("confirmed");
});
```

Características:

- Testa comportamento que usuários/chamadores se importam
- Usa apenas API pública
- Sobrevive a refatorações internas
- Descreve O QUÊ, não COMO
- Uma asserção lógica por teste

## Maus Testes

**Testes de detalhes de implementação**: Acoplados à estrutura interna.

```typescript
// RUIM: Testa detalhes de implementação
test("checkout chama paymentService.process", async () => {
  const mockPayment = jest.mock(paymentService);
  await checkout(cart, payment);
  expect(mockPayment.process).toHaveBeenCalledWith(cart.total);
});
```

Sinais de alerta:

- Mockando colaboradores internos
- Testando métodos privados
- Verificando contagem/ordem de chamadas
- Teste quebra ao refatorar sem mudar o comportamento
- Nome do teste descreve COMO, não O QUÊ
- Verificar por meios externos em vez da interface

```typescript
// RUIM: Contorna a interface para verificar
test("createUser salva no banco de dados", async () => {
  await createUser({ name: "Alice" });
  const row = await db.query("SELECT * FROM users WHERE name = ?", ["Alice"]);
  expect(row).toBeDefined();
});

// BOM: Verifica através da interface
test("createUser torna o usuário recuperável", async () => {
  const user = await createUser({ name: "Alice" });
  const retrieved = await getUser(user.id);
  expect(retrieved.name).toBe("Alice");
});
```
