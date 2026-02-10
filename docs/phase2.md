## Phase 2: CRUD 게시판

### 2.1 Phase 2 개요

#### 🎯 목표

**"사용자들이 게시글을 작성하고, 댓글을 달고, 좋아요를 누를 수 있는 커뮤니티 만들기"**

#### 📦 만들 것들

| 항목 | 설명 | 파일 |
|------|------|------|
| **Board Entity** | 게시판 정보 (자유게시판, MUTI 타입별) | `Board.java` |
| **Post Entity** | 게시글 정보 | `Post.java` |
| **Comment Entity** | 댓글 정보 (계층형 구조) | `Comment.java` |
| **PostLike Entity** | 좋아요 정보 | `PostLike.java` |
| **Repository** | 데이터베이스 접근 (4개) | `*Repository.java` |
| **Service** | 비즈니스 로직 (4개) | `*Service.java` |
| **Controller** | API 엔드포인트 (4개) | `*Controller.java` |
| **DTO** | 요청/응답 객체 (8개) | `dto/*` |

#### ⏱️ 예상 소요 시간

```
Week 4-5 (Day 22-35): 총 14일
├─ Day 22-25: Entity & Repository (4일)
├─ Day 26-29: Service & 비즈니스 로직 (4일)
├─ Day 30-33: Controller & API (4일)
└─ Day 34-35: 통합 테스트 & 정리 (2일)
```

**Phase 2 완료일**: 2026년 2월 9일 ✅

---

### 2.2 게시판 아키텍처 이해

#### 📊 데이터베이스 설계

**비유: 건물의 구조**

```
게시판 (Board) = 건물
├─ 자유게시판 = 1층 (누구나 이용)
├─ ESAP 게시판 = 2층 (ESAP 타입 사용자)
└─ IFDU 게시판 = 3층 (IFDU 타입 사용자)

각 층마다:
├─ 게시글 (Post) = 방
│   ├─ 제목, 내용
│   ├─ 조회수, 좋아요 수
│   └─ 댓글 (Comment) = 방 안의 메모들
│       ├─ 댓글
│       └─ 대댓글 (부모-자식 관계)
└─ 좋아요 (PostLike) = 좋아요 누른 기록
```

#### 🗄️ ERD (Entity Relationship Diagram)

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  users   │         │  boards  │         │  posts   │
│          │         │          │         │          │
│  id (PK) │         │  id (PK) │         │  id (PK) │
│  email   │         │  name    │         │  title   │
│  username│         │  type    │         │  content │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │
     │                    │ 1                  │ 1
     │                    │                    │
     │ 1                  │ N                  │ N
     │              ┌─────┴─────┐         ┌───┴──────┐
     │              │           │         │          │
     │              ▼           ▼         ▼          │
     │          ┌───────┐   ┌───────┐  ┌──────────┐ │
     │          │ posts │   │ posts │  │ comments │ │
     │          └───────┘   └───────┘  └──────────┘ │
     │                                               │
     │ 1                                             │ 1
     │                                               │
     │ N                                             │ N
┌────┴──────┐                                  ┌────┴──────┐
│ post_likes│                                  │ comments  │
│           │                                  │ (self FK) │
│  id (PK)  │                                  │ parent_id │
│  post_id  │                                  └───────────┘
│  user_id  │
└───────────┘

관계:
- Board 1 : N Post (하나의 게시판에 여러 게시글)
- User 1 : N Post (한 사용자가 여러 게시글 작성)
- Post 1 : N Comment (하나의 게시글에 여러 댓글)
- Post 1 : N PostLike (하나의 게시글에 여러 좋아요)
- Comment 1 : N Comment (댓글에 대댓글, 계층형 구조)
```

---

### 2.3 Entity 설계

#### 📋 Board Entity

**역할**: 게시판 종류 관리 (자유게시판, MUTI 타입별 게시판)

```java
@Entity
@Table(name = "boards")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;  // "자유게시판", "ESAP 게시판"

    @Column(columnDefinition = "TEXT")
    private String description;  // 게시판 설명

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", length = 20)
    private BoardType boardType;  // FREE, MUTI_TYPE

    @Enumerated(EnumType.STRING)
    @Column(name = "muti_type", length = 4)
    private MutiType mutiType;  // ESAP, IFDU 등 (MUTI_TYPE인 경우)

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
}
```

**설명:**
- `BoardType.FREE`: 자유게시판 (모두 이용 가능)
- `BoardType.MUTI_TYPE`: MUTI 타입별 게시판 (16개)
- `mutiType`: FREE인 경우 null, MUTI_TYPE인 경우 ESAP~IFDU 중 하나

#### 📝 Post Entity

**역할**: 게시글 정보 저장

```java
@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_board_created", columnList = "board_id, created_at"),
    @Index(name = "idx_user", columnList = "user_id")
})
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count")
    private Integer viewCount = 0;  // 조회수

    @Column(name = "like_count")
    private Integer likeCount = 0;  // 좋아요 수

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    // 비즈니스 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
```

**핵심 포인트:**
1. **지연 로딩 (Lazy Loading)**: `@ManyToOne(fetch = FetchType.LAZY)`
   - Board, User는 필요할 때만 조회 (N+1 문제 방지)
2. **인덱스**: 게시판별, 작성일별 빠른 조회
3. **CASCADE**: 게시글 삭제 시 댓글, 좋아요도 함께 삭제
4. **비즈니스 메서드**: 조회수, 좋아요 수 증가/감소 로직 캡슐화

#### 💬 Comment Entity (계층형 구조)

**역할**: 댓글 및 대댓글 저장

```java
@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_post", columnList = "post_id"),
    @Index(name = "idx_parent", columnList = "parent_comment_id")
})
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ⭐ 계층형 구조: 부모 댓글 참조 (null이면 최상위 댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parentComment;

    // 대댓글 목록
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 비즈니스 메서드
    public boolean isAuthor(Long userId) {
        return this.user.getId().equals(userId);
    }
}
```

**계층형 구조 예시:**

```
댓글 1 (parentComment = null)
├─ 대댓글 1-1 (parentComment = 댓글1)
├─ 대댓글 1-2 (parentComment = 댓글1)
│  └─ 대댓글 1-2-1 (parentComment = 대댓글1-2)
└─ 대댓글 1-3 (parentComment = 댓글1)

댓글 2 (parentComment = null)
└─ 대댓글 2-1 (parentComment = 댓글2)
```

**조회 방법:**
```java
// 최상위 댓글만 조회
List<Comment> topComments = commentRepository.findByPostIdAndParentCommentIsNull(postId);

// 특정 댓글의 대댓글 조회
List<Comment> replies = commentRepository.findByParentCommentId(parentId);
```

#### ❤️ PostLike Entity

**역할**: 좋아요 기록 (중복 방지)

```java
@Entity
@Table(name = "post_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

**핵심:**
- `@UniqueConstraint`: 같은 사용자가 같은 게시글에 중복 좋아요 방지
- 좋아요 토글: 있으면 삭제, 없으면 추가

---

### 2.4 Repository 구현

#### 📦 BoardRepository

```java
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 이름으로 게시판 찾기
    Optional<Board> findByName(String name);

    // 게시판 타입으로 조회
    List<Board> findByBoardType(BoardType boardType);

    // MUTI 타입으로 조회
    Optional<Board> findByMutiType(MutiType mutiType);
}
```

#### 📦 PostRepository

```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시판별 게시글 조회 (페이징)
    @Query("SELECT p FROM Post p " +
           "JOIN FETCH p.user " +
           "WHERE p.board.id = :boardId " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    // 사용자별 게시글 조회
    List<Post> findByUserId(Long userId);

    // 조회수 증가 (벌크 연산)
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
```

**포인트:**
- `JOIN FETCH`: N+1 문제 해결 (User 정보를 한 번에 조회)
- `@Modifying`: UPDATE 쿼리 실행

#### 📦 CommentRepository

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글별 댓글 조회
    List<Comment> findByPostId(Long postId);

    // 최상위 댓글만 조회
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

    // 대댓글 조회
    List<Comment> findByParentCommentId(Long parentId);

    // 사용자별 댓글 조회
    List<Comment> findByUserId(Long userId);

    // 계층형 댓글 조회 (성능 최적화)
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.user " +
           "LEFT JOIN FETCH c.parentComment " +
           "WHERE c.post.id = :postId " +
           "ORDER BY " +
           "COALESCE(c.parentComment.id, c.id), " +
           "c.createdAt ASC")
    List<Comment> findByPostIdWithUserOrderByHierarchy(@Param("postId") Long postId);
}
```

**계층형 정렬 로직:**
```sql
ORDER BY
  COALESCE(c.parentComment.id, c.id),  -- 부모 ID 기준 그룹화
  c.createdAt ASC                       -- 같은 그룹 내에서 시간순
```

결과:
```
1. 댓글1 (id=1, parent=null) → COALESCE(null, 1) = 1
2. 대댓글1-1 (id=4, parent=1) → COALESCE(1, 4) = 1
3. 대댓글1-2 (id=5, parent=1) → COALESCE(1, 5) = 1
4. 댓글2 (id=2, parent=null) → COALESCE(null, 2) = 2
5. 대댓글2-1 (id=6, parent=2) → COALESCE(2, 6) = 2
```

#### 📦 PostLikeRepository

```java
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 좋아요 존재 여부
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 조회
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 삭제
    @Modifying
    @Transactional
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // 게시글별 좋아요 수
    long countByPostId(Long postId);

    // 게시글별 좋아요 목록
    List<PostLike> findByPostId(Long postId);

    // 사용자별 좋아요 목록
    List<PostLike> findByUserId(Long userId);
}
```

---

### 2.5 Service 비즈니스 로직

#### 🎯 PostService - 게시글 서비스

**핵심 기능:**
1. 게시글 작성 (인증 필요)
2. 게시글 조회 (조회수 증가)
3. 게시글 수정 (작성자만)
4. 게시글 삭제 (작성자만)
5. 게시글 목록 (페이징)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardService boardService;
    private final UserRepository userRepository;

    // 1. 게시글 작성
    @Transactional
    public PostDetailDto createPost(CreatePostRequest request, Long userId) {
        // 게시판 조회
        Board board = boardService.getBoardEntity(request.getBoardId());

        // 사용자 조회
        User user = getUserEntity(userId);

        // 게시글 생성
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post saved = postRepository.save(post);

        return PostDetailDto.from(saved);
    }

    // 2. 게시글 조회 (조회수 증가)
    @Transactional
    public PostDetailDto getPost(Long id, Long currentUserId) {
        Post post = getPostEntity(id);

        // 조회수 증가
        post.incrementViewCount();

        // 작성자 여부 확인
        boolean isAuthor = post.isAuthor(currentUserId);

        return PostDetailDto.from(post, isAuthor);
    }

    // 3. 게시글 수정 (작성자만)
    @Transactional
    public PostDetailDto updatePost(Long id, UpdatePostRequest request, Long userId) {
        Post post = getPostEntity(id);

        // 권한 검증
        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.POST_UPDATE_FORBIDDEN);
        }

        // 수정
        post.update(request.getTitle(), request.getContent());

        return PostDetailDto.from(post);
    }

    // 4. 게시글 삭제 (작성자만)
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = getPostEntity(id);

        // 권한 검증
        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.POST_DELETE_FORBIDDEN);
        }

        postRepository.delete(post);
    }

    // 5. 게시글 목록 (페이징)
    public Page<PostDto> getPostsByBoard(Long boardId, Pageable pageable, Long currentUserId) {
        Page<Post> posts = postRepository.findByBoardId(boardId, pageable);

        return posts.map(post -> PostDto.from(post, post.isAuthor(currentUserId)));
    }

    // Helper 메서드
    private Post getPostEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
```

**비즈니스 로직 포인트:**
1. **조회수 증가**: 게시글 조회 시마다 자동 증가
2. **권한 검증**: 수정/삭제는 작성자만 가능
3. **페이징**: 게시판별 게시글 목록은 페이징 처리
4. **작성자 여부**: 응답에 isAuthor 필드 포함 (프론트에서 수정/삭제 버튼 표시용)

#### ❤️ PostLikeService - 좋아요 서비스

**핵심 기능:**
1. 좋아요 토글 (있으면 삭제, 없으면 추가)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 토글
    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        // 게시글 조회
        Post post = getPostEntity(postId);

        // 사용자 조회
        User user = getUserEntity(userId);

        // 좋아요 존재 여부 확인
        boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        if (exists) {
            // 좋아요 취소
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.decrementLikeCount();

            return LikeResponse.builder()
                    .isLiked(false)
                    .likeCount(post.getLikeCount())
                    .build();
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            postLikeRepository.save(postLike);
            post.incrementLikeCount();

            return LikeResponse.builder()
                    .isLiked(true)
                    .likeCount(post.getLikeCount())
                    .build();
        }
    }

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
```

**토글 로직:**
```
1. 좋아요 존재 확인
   └─ EXISTS → 삭제 + likeCount--
   └─ NOT EXISTS → 추가 + likeCount++

2. 응답
   {
     "isLiked": true/false,
     "likeCount": 10
   }
```

#### 💬 CommentService - 댓글 서비스

**핵심 기능:**
1. 댓글 작성
2. 대댓글 작성 (부모 댓글 ID 지정)
3. 댓글 삭제 (작성자만)
4. 게시글별 댓글 조회 (계층형)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 1. 댓글 작성
    @Transactional
    public CommentDto createComment(Long postId, CreateCommentRequest request, Long userId) {
        Post post = getPostEntity(postId);
        User user = getUserEntity(userId);

        // 부모 댓글 검증 (대댓글인 경우)
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = getCommentEntity(request.getParentCommentId());

            // 부모 댓글이 같은 게시글에 속하는지 확인
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException(ErrorCode.COMMENT_PARENT_MISMATCH);
            }
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .parentComment(parentComment)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentDto.from(saved, true);
    }

    // 2. 댓글 삭제 (작성자만)
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentEntity(commentId);

        // 권한 검증
        if (!comment.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

    // 3. 게시글별 댓글 조회 (계층형)
    public List<CommentDto> getCommentsByPost(Long postId, Long currentUserId) {
        // 게시글 존재 확인
        getPostEntity(postId);

        // 계층형 댓글 조회
        List<Comment> comments = commentRepository
                .findByPostIdWithUserOrderByHierarchy(postId);

        return comments.stream()
                .map(comment -> CommentDto.from(comment, comment.isAuthor(currentUserId)))
                .collect(Collectors.toList());
    }

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Comment getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
```

---

### 2.6 Controller API 설계

#### 📡 BoardController

```java
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 목록 조회
    @GetMapping
    public ApiResponse<List<BoardDto>> getAllBoards() {
        List<BoardDto> boards = boardService.getAllBoards();
        return ApiResponse.success(boards);
    }

    // 게시판 생성 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<BoardDto> createBoard(@Valid @RequestBody CreateBoardRequest request) {
        BoardDto board = boardService.createBoard(request);
        return ApiResponse.success(board);
    }
}
```

#### 📡 PostController

```java
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    // 게시글 작성
    @PostMapping
    public ApiResponse<PostDetailDto> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        PostDetailDto post = postService.createPost(request, principal.getId());
        return ApiResponse.success(post);
    }

    // 게시글 조회
    @GetMapping("/{id}")
    public ApiResponse<PostDetailDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = (principal != null) ? principal.getId() : null;
        PostDetailDto post = postService.getPost(id, userId);
        return ApiResponse.success(post);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ApiResponse<PostDetailDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        PostDetailDto post = postService.updatePost(id, request, principal.getId());
        return ApiResponse.success(post);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        postService.deletePost(id, principal.getId());
        return ApiResponse.success(null);
    }

    // 게시판별 게시글 목록
    @GetMapping
    public ApiResponse<Page<PostDto>> getPostsByBoard(
            @RequestParam Long boardId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = (principal != null) ? principal.getId() : null;
        Page<PostDto> posts = postService.getPostsByBoard(boardId, pageable, userId);
        return ApiResponse.success(posts);
    }

    // 좋아요 토글
    @PostMapping("/{id}/like")
    public ApiResponse<LikeResponse> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LikeResponse response = postLikeService.toggleLike(id, principal.getId());
        return ApiResponse.success(response);
    }
}
```

---

### 2.7 Flyway 마이그레이션

#### V5__create_board_tables.sql

```sql
-- boards 테이블
CREATE TABLE boards (
    board_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    board_type VARCHAR(20) NOT NULL,
    muti_type VARCHAR(4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_board_name UNIQUE (name),
    CONSTRAINT unique_muti_type UNIQUE (muti_type)
);

-- posts 테이블
CREATE TABLE posts (
    post_id BIGSERIAL PRIMARY KEY,
    board_id BIGINT NOT NULL REFERENCES boards(board_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_posts_board_created ON posts(board_id, created_at DESC);
CREATE INDEX idx_posts_user ON posts(user_id);

-- comments 테이블
CREATE TABLE comments (
    comment_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    parent_comment_id BIGINT REFERENCES comments(comment_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_parent ON comments(parent_comment_id);

-- post_likes 테이블
CREATE TABLE post_likes (
    like_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_post_like UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_likes_post ON post_likes(post_id);
CREATE INDEX idx_post_likes_user ON post_likes(user_id);
```

#### V6__insert_initial_boards.sql

```sql
-- 자유게시판
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('자유게시판', '자유롭게 이야기를 나눠보세요!', 'FREE', NULL);

-- MUTI 타입별 게시판 (16개)
INSERT INTO boards (name, description, board_type, muti_type) VALUES
('ESAP 게시판', '감성적이고 잔잔한 어쿠스틱 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESAP'),
('ESAU 게시판', '감성적이고 잔잔한 어쿠스틱 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESAU'),
-- ... (14개 더)
('IFDU 게시판', '연주 중심의 빠른 디지털 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'IFDU');
```

---

### 2.8 테스트 작성

Phase 2에서는 **57개의 테스트**를 작성했습니다:

#### Repository 테스트 (37개)

```java
@DataJpaTest
@Import(TestJpaConfig.class)
@DisplayName("PostRepository 통합 테스트")
class PostRepositoryTest {

    @Test
    @DisplayName("게시글 저장 및 조회")
    void savePost() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        // when
        Post saved = postRepository.save(post);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
    }
}
```

#### Service 테스트 (20개)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 작성 - 성공")
    void createPost_Success() {
        // given
        given(boardService.getBoardEntity(any())).willReturn(board);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(postRepository.save(any())).willReturn(savedPost);

        // when
        PostDetailDto result = postService.createPost(request, userId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        verify(postRepository).save(any(Post.class));
    }
}
```

---

### 2.9 Phase 2 완료 체크리스트

```
✅ Entity 설계 (Board, Post, Comment, PostLike)
✅ Repository 구현 (4개)
✅ Service 비즈니스 로직 (4개)
✅ Controller API 엔드포인트 (4개)
✅ DTO 정의 (8개)
✅ Flyway 마이그레이션 (V5, V6)
✅ 초기 데이터 생성 (17개 게시판)
✅ Repository 테스트 (37개)
✅ Service 테스트 (20개)
✅ 전체 테스트 통과 (57개)
✅ 실제 동작 확인 (GET /api/v1/boards)
✅ feature/board → dev 머지
✅ 원격 저장소 push
```

---

### 2.10 Phase 2 주요 학습 내용

#### 🎓 배운 개념들

1. **JPA 연관관계**
   - @OneToMany, @ManyToOne
   - FetchType.LAZY (지연 로딩)
   - CASCADE, orphanRemoval

2. **계층형 구조**
   - Self-referencing Entity
   - 부모-자식 관계 구현
   - 계층형 정렬 쿼리

3. **성능 최적화**
   - N+1 문제 해결 (JOIN FETCH)
   - 인덱스 설계
   - 벌크 연산 (@Modifying)

4. **비즈니스 로직**
   - 권한 검증 (작성자만 수정/삭제)
   - 조회수 증가
   - 좋아요 토글

5. **페이징**
   - Pageable, Page<T>
   - 정렬 기준 지정

6. **테스트**
   - @DataJpaTest (Repository)
   - @ExtendWith(MockitoExtension.class) (Service)
   - given-when-then 패턴

---

### 2.11 다음 단계

Phase 2 완료! 이제 Phase 3으로 진행합니다:

**Phase 3: Music/Playlist 도메인**
- Music Entity: 음악 정보
- Playlist Entity: 플레이리스트
- Spotify API 연동
- 타입별 음악 추천

준비되셨으면 **"Phase 3 시작!"** 이라고 말씀해주세요! 🚀

---

