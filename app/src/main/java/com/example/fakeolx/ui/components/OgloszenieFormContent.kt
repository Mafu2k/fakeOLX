package com.example.fakeolx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.ui.viewmodel.FormErrors
import com.example.fakeolx.ui.viewmodel.OgloszenieForm

@Composable
fun OgloszenieFormContent(
    form: OgloszenieForm,
    errors: FormErrors,
    onFormChange: (OgloszenieForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = form.tytul,
            onValueChange = { onFormChange(form.copy(tytul = it)) },
            label = { Text("Tytuł *") },
            placeholder = { Text("np. Rower górski Kross, rama M") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            isError = errors.tytul != null,
            supportingText = {
                Text(errors.tytul ?: "${form.tytul.length}/${OgloszenieForm.MAX_TYTUL_LENGTH}")
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        SelectField(
            label = "Kategoria *",
            value = form.kategoria.displayName,
            options = Kategoria.entries.map { it.displayName },
            onSelect = { onFormChange(form.withKategoria(Kategoria.fromDisplayName(it))) },
            leadingIcon = Icons.Default.LocalOffer
        )

        OutlinedTextField(
            value = form.cena,
            onValueChange = { onFormChange(form.copy(cena = it)) },
            label = { Text(form.kategoria.cenaLabel) },
            placeholder = { Text("np. 120 albo 49,99") },
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            isError = errors.cena != null,
            supportingText = { Text(errors.cena ?: "Wpisz 0, jeśli oddajesz za darmo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.miasto,
            onValueChange = { onFormChange(form.copy(miasto = it)) },
            label = { Text("Miasto") },
            placeholder = { Text("np. Gliwice") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            supportingText = { Text("Zostaw puste, jeśli ogłoszenie dotyczy pracy lub zajęć online") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (form.kategoria.wymagaSzczegolow) {
            val isPraca = form.kategoria == Kategoria.PRACA
            OutlinedTextField(
                value = form.specjalizacja,
                onValueChange = { onFormChange(form.copy(specjalizacja = it)) },
                label = { Text(form.kategoria.specjalizacjaLabel) },
                placeholder = { Text(if (isPraca) "np. Młodszy programista Android" else "np. Matematyka, liceum") },
                leadingIcon = {
                    Icon(if (isPraca) Icons.Default.Work else Icons.Default.School, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            SelectField(
                label = form.kategoria.trybLabel,
                value = form.tryb,
                options = form.kategoria.trybOptions,
                onSelect = { onFormChange(form.copy(tryb = it)) },
                leadingIcon = Icons.Default.Tune,
                placeholder = "Wybierz z listy"
            )
        }

        OutlinedTextField(
            value = form.opis,
            onValueChange = { onFormChange(form.copy(opis = it)) },
            label = { Text("Opis *") },
            placeholder = { Text("Opisz stan, wymiary, warunki odbioru…") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
            isError = errors.opis != null,
            supportingText = errors.opis?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            minLines = 4,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.zdjecieUrl,
            onValueChange = { onFormChange(form.copy(zdjecieUrl = it)) },
            label = { Text("Link do zdjęcia") },
            placeholder = { Text("https://…") },
            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
            isError = errors.zdjecieUrl != null,
            supportingText = { Text(errors.zdjecieUrl ?: "Opcjonalnie, adres obrazka w internecie") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Pola oznaczone * są wymagane",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
