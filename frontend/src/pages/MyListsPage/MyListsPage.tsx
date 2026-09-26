import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaChevronDown, FaListUl, FaPlus, FaTimes } from "react-icons/fa";
import "./MyListsPage.css";

const API_URL = import.meta.env.VITE_API_URL;

type MediaItem = {
  id: number;
  title: string;
  description: string | null;
  mediaType: "MOVIE" | "TV_SHOW" | "BOOK" | "GAME";
  releaseDate: string | null;
};

type MediaList = {
  id: number;
  name: string;
  description: string;
  // The current MediaListResponse omits media. Missing items are not an empty list.
  media?: MediaItem[];
};

const mediaTypeLabels = {
  MOVIE: "Movie",
  TV_SHOW: "TV show",
  BOOK: "Book",
  GAME: "Game",
};

function MyListsPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const [lists, setLists] = useState<MediaList[]>([]);
  const [expandedIds, setExpandedIds] = useState<number[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [statusMessage, setStatusMessage] = useState("");
  const [loadAttempt, setLoadAttempt] = useState(0);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [createError, setCreateError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const createButtonRef = useRef<HTMLButtonElement>(null);
  const nameInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!token || !Number.isSafeInteger(Number(userId)) || Number(userId) <= 0) {
      navigate("/login", { replace: true });
      return;
    }

    const controller = new AbortController();

    async function loadLists() {
      setIsLoading(true);
      setLoadError(null);

      try {
        const response = await fetch(`${API_URL}/media-lists/user/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
          signal: controller.signal,
        });

        if (response.status === 401) {
          navigate("/login", { replace: true });
          return;
        }

        if (!response.ok) {
          throw new Error("Your lists could not be loaded. Please try again.");
        }

        const mediaLists: MediaList[] = await response.json();
        if (!controller.signal.aborted) {
          setLists(mediaLists);
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setLoadError(error instanceof Error ? error.message : "The server could not be reached.");
        }
      } finally {
        if (!controller.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    void loadLists();
    return () => controller.abort();
  }, [token, userId, navigate, loadAttempt]);

  useEffect(() => {
    if (showCreateForm) {
      nameInputRef.current?.focus();
    }
  }, [showCreateForm]);

  function toggleList(id: number) {
    setExpandedIds((current) =>
      current.includes(id) ? current.filter((expandedId) => expandedId !== id) : [...current, id]
    );
  }

  async function handleDelete(list: MediaList) {
    if (deletingId !== null || !window.confirm(`Delete "${list.name}"? This cannot be undone.`)) {
      return;
    }

    setDeletingId(list.id);
    setActionError(null);
    setStatusMessage("");

    try {
      const response = await fetch(`${API_URL}/media-lists/${list.id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 401) {
        navigate("/login", { replace: true });
        return;
      }

      if (!response.ok) {
        setActionError(response.status === 403
          ? "You do not have permission to delete this list."
          : `Could not delete "${list.name}". Please try again.`);
        return;
      }

      setLists((current) => current.filter((item) => item.id !== list.id));
      setExpandedIds((current) => current.filter((id) => id !== list.id));
      setStatusMessage(`Deleted "${list.name}".`);
      createButtonRef.current?.focus();
    } catch {
      setActionError("The server could not be reached. Your list has not been removed from the page.");
    } finally {
      setDeletingId(null);
    }
  }

  async function handleCreate(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (isCreating) return;

    const trimmedName = name.trim();
    const trimmedDescription = description.trim();
    if (!trimmedName || !trimmedDescription) {
      setCreateError("Enter a name and description for your list.");
      return;
    }

    setIsCreating(true);
    setCreateError(null);
    setStatusMessage("");

    try {
      const response = await fetch(`${API_URL}/media-lists`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name: trimmedName, description: trimmedDescription }),
      });

      if (response.status === 401) {
        navigate("/login", { replace: true });
        return;
      }

      if (!response.ok) {
        setCreateError("Your list could not be created. Please try again.");
        return;
      }

      const newList: MediaList = await response.json();
      setLists((current) => [...current, newList]);
      setName("");
      setDescription("");
      setShowCreateForm(false);
      setStatusMessage(`Created "${newList.name}".`);
      createButtonRef.current?.focus();
    } catch {
      setCreateError("The server could not be reached. Please try again.");
    } finally {
      setIsCreating(false);
    }
  }

  return (
    <div className="my-lists-page">
      <header className="my-lists-header">
        <Link className="my-lists-brand" to="/dashboard">MediaMatch</Link>
        <Link className="my-lists-back" to="/dashboard">Back to dashboard</Link>
      </header>

      <main className="my-lists-main">
        <div className="my-lists-heading">
          <h1>Your Lists</h1>
          <p>Keep your favorites together. Select a list to take a closer look.</p>
        </div>

        <p className="my-lists-status" role="status">{statusMessage}</p>
        {actionError && <p className="my-lists-error" role="alert">{actionError}</p>}

        <section className="my-lists-collection" aria-label="Your media lists" aria-busy={isLoading}>
          {isLoading && <p className="my-lists-notice" role="status">Loading your lists...</p>}

          {loadError && (
            <div className="my-lists-notice">
              <p className="my-lists-error" role="alert">{loadError}</p>
              <button className="my-lists-secondary" type="button" onClick={() => setLoadAttempt((current) => current + 1)}>
                Try again
              </button>
            </div>
          )}

          {!isLoading && !loadError && lists.length === 0 && (
            <div className="my-lists-empty">
              <FaListUl aria-hidden="true" />
              <h2>A home for your favorites</h2>
              <p>You don&apos;t have any lists yet. Create your first one below.</p>
            </div>
          )}

          {!isLoading && !loadError && lists.map((list) => {
            const isExpanded = expandedIds.includes(list.id);

            return (
              <article className={`my-lists-card${isExpanded ? " my-lists-card-expanded" : ""}`} key={list.id}>
                <div className="my-lists-card-header">
                  <h2>
                    <button
                      className="my-lists-toggle"
                      type="button"
                      aria-expanded={isExpanded}
                      aria-controls={`list-content-${list.id}`}
                      onClick={() => toggleList(list.id)}
                    >
                      <span className="my-lists-list-icon"><FaListUl aria-hidden="true" /></span>
                      <span className="my-lists-summary">
                        <span className="my-lists-name">{list.name}</span>
                        <span className="my-lists-preview">{list.description}</span>
                      </span>
                      <FaChevronDown className="my-lists-chevron" aria-hidden="true" />
                    </button>
                  </h2>
                  <button
                    className="my-lists-delete"
                    type="button"
                    aria-label={`Delete ${list.name}`}
                    disabled={deletingId !== null}
                    onClick={() => void handleDelete(list)}
                  >
                    <FaTimes aria-hidden="true" />
                    <span>{deletingId === list.id ? "Deleting..." : "Delete"}</span>
                  </button>
                </div>

                <div className="my-lists-details" id={`list-content-${list.id}`} hidden={!isExpanded}>
                  <p className="my-lists-description">{list.description}</p>
                  <h3>Media in this list</h3>
                  {!Array.isArray(list.media) ? (
                    <p className="my-lists-unavailable">Media items for this list are currently unavailable.</p>
                  ) : list.media.length === 0 ? (
                    <p className="my-lists-unavailable">No media has been added to this list yet.</p>
                  ) : (
                    <ul className="my-lists-media">
                      {list.media.map((item) => (
                        <li key={item.id}>
                          <div className="my-lists-media-heading">
                            <h4>{item.title}</h4>
                            <span className="my-lists-media-type">{mediaTypeLabels[item.mediaType]}</span>
                          </div>
                          {item.releaseDate && <p className="my-lists-media-year">Released {item.releaseDate.slice(0, 4)}</p>}
                          {item.description && <p className="my-lists-media-description">{item.description}</p>}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </article>
            );
          })}
        </section>

        <div className="my-lists-create-area">
          <button
            className="my-lists-create-button"
            ref={createButtonRef}
            type="button"
            disabled={isLoading || Boolean(loadError) || isCreating}
            aria-expanded={showCreateForm}
            aria-controls="my-lists-create-form"
            onClick={() => {
              setShowCreateForm((current) => !current);
              setCreateError(null);
            }}
          >
            <FaPlus aria-hidden="true" /> Create New List
          </button>

          <form className="my-lists-create-form" id="my-lists-create-form" hidden={!showCreateForm} onSubmit={handleCreate}>
            <h2>Create a new list</h2>
            <label htmlFor="my-lists-name">List name</label>
            <input
              ref={nameInputRef}
              id="my-lists-name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="e.g. Weekend watchlist"
              maxLength={255}
              disabled={isCreating}
              required
            />
            <label htmlFor="my-lists-description">Description</label>
            <textarea
              id="my-lists-description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="What belongs in this list?"
              rows={3}
              maxLength={300}
              aria-describedby="my-lists-description-limit"
              disabled={isCreating}
              required
            />
            <p className="my-lists-character-count" id="my-lists-description-limit">{description.length}/300 characters</p>
            {createError && <p className="my-lists-error" role="alert">{createError}</p>}
            <div className="my-lists-form-actions">
              <button className="my-lists-secondary" type="button" disabled={isCreating} onClick={() => {
                setShowCreateForm(false);
                setCreateError(null);
                createButtonRef.current?.focus();
              }}>Cancel</button>
              <button className="my-lists-primary" type="submit" disabled={isCreating}>
                {isCreating ? "Creating..." : "Create list"}
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}

export default MyListsPage;
