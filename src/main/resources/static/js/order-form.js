// Arma el pedido en el navegador y lo envía a la API (POST /api/orders).
// No calcula precios ni valida reglas: eso lo hace el backend.
(() => {
    const cart = [];
    const cartList = document.getElementById('cart-items');
    const nameInput = document.getElementById('customer-name');
    const submitButton = document.getElementById('submit-order');
    const errorBox = document.getElementById('order-error');

    document.querySelectorAll('.product').forEach(card => {
        card.querySelector('.add-item').addEventListener('click', () => {
            const sizeSelect = card.querySelector('.size');
            const checked = [...card.querySelectorAll('.modifier:checked')];
            cart.push({
                productId: Number(card.dataset.productId),
                productName: card.dataset.productName,
                size: sizeSelect ? sizeSelect.value : null,
                sizeLabel: sizeSelect ? sizeSelect.selectedOptions[0].dataset.label : null,
                modifiers: checked.map(input => input.value),
                modifierLabels: checked.map(input => input.dataset.label)
            });
            checked.forEach(input => { input.checked = false; });
            render();
        });
    });

    function render() {
        cartList.replaceChildren();
        if (cart.length === 0) {
            const empty = document.createElement('li');
            empty.className = 'empty';
            empty.textContent = 'Aún no hay productos.';
            cartList.append(empty);
            return;
        }
        cart.forEach((item, index) => {
            const li = document.createElement('li');
            const text = document.createElement('span');
            let description = item.productName;
            if (item.sizeLabel) description += ' · ' + item.sizeLabel;
            if (item.modifierLabels.length) description += ' (' + item.modifierLabels.join(', ') + ')';
            text.textContent = description;

            const remove = document.createElement('button');
            remove.type = 'button';
            remove.className = 'btn link';
            remove.textContent = 'Quitar';
            remove.addEventListener('click', () => {
                cart.splice(index, 1);
                render();
            });
            li.append(text, remove);
            cartList.append(li);
        });
    }

    function showError(message) {
        errorBox.textContent = message;
        errorBox.hidden = false;
    }

    submitButton.addEventListener('click', async () => {
        errorBox.hidden = true;
        // Se envía aunque el carrito esté vacío: el backend debe rechazarlo (regla del reto).
        const body = {
            customerName: nameInput.value,
            items: cart.map(({ productId, size, modifiers }) => ({ productId, size, modifiers }))
        };
        submitButton.disabled = true;
        try {
            const response = await fetch('/api/orders', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                showError(data.message || 'No se pudo registrar el pedido (error ' + response.status + ').');
                return;
            }
            window.location.href = '/?nuevo=' + data.id;
        } catch (e) {
            showError('No hay conexión con el servidor.');
        } finally {
            submitButton.disabled = false;
        }
    });

    render();
})();
