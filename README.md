Przeznaczeniem projektu jest rejestrowanie prezentów dla dzieci.

Dziecko - imię, nazwisko, data urodzenia
Prezent - nazwa, cena

Każde dziecko może mieć nie więcej niż 3 prezenty.


API:

- dodanie dziecka
- pobranie wszystkich dzieci
- pobranie konkretnego dziecka
- update dziecka
- usunięcie dziecka

- dodanie prezentu konkretnemu dziecku
- pobranie prezentów konkretnego dziecka
- pobranie konkretnego prezentu od konkretnego dziecka
- update prezentu u konkretnego dziecka
- usunięcie prezentu konkretnemu dziecku

Zaprojektuj rozwiązanie w taki sposób aby można było obsługiwać chłopców (ulubiony sport) oraz dziewczynki (kolor sukienki)

rozwiązanie ma być również gotowe na wygodne dodawanie do obsługi nowych typów dziecka
- dla childa paginacja z dowlnym filtrowaniemm dynamicznym
- informacja o dziecku zwracana z iloscia prezentow (n+1, paginacja w pamieci, problemy wydajnosciowe)
- MessageFormat for logs

-mozemy wyszukac po ilosci prezentow (rozwizania: widok)
-ma byc wykonowane rezcznie i pod spodem 2 (query i count)
- i zeby mozna bylo wyszukiwac od i do (rozprasowanie sting min : max)


## Cykliczne przetwarzanie drogich prezentów (>100 PLN)

### Wymagania:
- mechanizm uruchamiany cyklicznie (raz na dobę o 2:00)
- logowanie informacji o wszystkich prezentach dzieci, które przekraczają kwotę 100PLN
- każde dziecko otrzymuje informację tylko raz (na cykl przetwarzania), ale ze wszystkimi jego prezentami które przekraczają tą kwotę
- format logu: `imię nazwisko - [prezent1 cena, prezent2 cena, ...]`
- obsługa 2 mln dzieci z prezentami bez przekroczenia 250 MB pamięci

### Implementacja z kursorami:

**Klasy:**
- `ExpensiveGiftNotificationService` - service przetwarzający dzieci ze streamowaniem
- `ExpensiveGiftScheduler` - scheduler uruchamiający zadanie codziennie o 2:00
- `AdminController` - endpoint do manualnego testowania (`POST /api/v1/admin/process-expensive-gifts`)

**Kluczowe techniki:**
1. **Stream z kursorami** (`Stream<Child>`) - zamiast `List<Child>` aby nie ładować wszystkich dzieci do pamięci
2. **`@QueryHints(HINT_FETCH_SIZE = 100)`** - baza pobiera dane batchami po 100 rekordów
3. **`LEFT JOIN FETCH c.gifts`** - unikanie N+1, prezenty pobierane razem z dzieckiem
4. **`WHERE g.price > 100`** - filtrowanie po stronie bazy (nie ładuje dzieci bez drogich prezentów)
5. **`@Transactional(readOnly = true)`** - kursor musi być w transakcji
6. **`try-with-resources`** - automatyczne zamykanie streama i zwalnianie zasobów

**Zużycie pamięci:** ~1-2 MB (tylko aktualny batch 100 dzieci w pamięci)

**Cron scheduling:** `0 0 2 * * ?` = codziennie o 2:00
