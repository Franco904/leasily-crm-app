# Design de Interface para Testabilidade

Boas interfaces tornam os testes naturais:

1. **Aceite dependências, não as crie**

   ```typescript
   // Testável
   function processOrder(order, paymentGateway) {}

   // Difícil de testar
   function processOrder(order) {
     const gateway = new StripeGateway();
   }
   ```

2. **Retorne resultados, não produza efeitos colaterais**

   ```typescript
   // Testável
   function calculateDiscount(cart): Discount {}

   // Difícil de testar
   function applyDiscount(cart): void {
     cart.total -= discount;
   }
   ```

3. **Superfície pequena**
   - Menos métodos = menos testes necessários
   - Menos parâmetros = configuração de teste mais simples
