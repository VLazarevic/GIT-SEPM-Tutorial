# GIT-SEPM-Tutorial
Help students understand git

## Merge vs. Rebase 

### Merge
- **Was passiert?**  
  Führt zwei Branches zusammen, indem ein **Merge-Commit** erstellt wird.
- **Historie:**  
  Bleibt vollständig erhalten (verzweigt).
- **Vorteile:**  
  Sicher, transparent, gut für Teamarbeit.
- **Nachteil:**  
  Commit-Historie kann unübersichtlich werden.

A---B---C---D (main)
\
E---F---G (feature)


---

### Rebase
- **Was passiert?**  
  Verschiebt die Commits eines Branches auf einen neuen Basis-Commit.
- **Historie:**  
  Wird **neu geschrieben** (linear).
- **Vorteile:**  
  Saubere, lineare Historie.
- **Nachteil:**  
  Kann problematisch sein bei bereits geteilten Branches.

A---B---C---D---E'---F'---G' (feature)

---

### Zusammenfassung
- **Merge** = „Historie behalten“
- **Rebase** = „Historie umschreiben“