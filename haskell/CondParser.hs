{-# LANGUAGE TupleSections #-}
module CondParser where

import Data.List (nub)
import Data.Char (isLetter, isSpace)
import Data.Monoid

-- -------
-- Helpers
-- -------

-- logical implication
(==>) :: Bool -> Bool -> Bool
False  ==>  False  =  True
False  ==>  True   =  True
True   ==>  False  =  False
True   ==>  True   =  True

-- -----------------
-- Naive SAT solving
-- -----------------

-- logical variables
type Variable
  = String

-- logical models
type Model
  = Variable -> Bool

-- enumerating all models for a set of variables
models :: [Variable] -> [Model]
models [] 
  =  [\x -> error ("unbound variable " ++ x)]
  
models (v:vs) 
  =  [  \x -> if x == v then truth else model x
     |  truth <- [False, True]
     ,  model <- models vs ]

-- logical formulae
data Formula
  =  Truth
  |  Falsity
  |  Var Variable
  |  Not Formula
  |  Formula :&&: Formula
  |  Formula :||: Formula
  |  Formula :=>: Formula
  deriving (Eq, Show)

-- free variables of a formula
fv :: Formula -> [Variable]
fv = nub . fv' where
  fv' (Truth)     =  []
  fv' (Falsity)   =  []
  fv' (Var v)     =  [v]
  fv' (Not f)     =  fv' f
  fv' (f :&&: g)  =  fv' f ++ fv' g
  fv' (f :||: g)  =  fv' f ++ fv' g
  fv' (f :=>: g)  =  fv' f ++ fv' g

-- models relationship
(|=) :: Model -> Formula -> Bool
m |=  Truth         =  True
m |=  Falsity       =  False
m |=  Var s         =  m s
m |=  Not f         =  not (m |= f)
m |=  (f  :&&:  g)  =  (m |= f)  &&   (m |= g)
m |=  (f  :||:  g)  =  (m |= f)  ||   (m |= g)
m |=  (f  :=>:  g)  =  (m |= f)  ==>  (m |= g)

-- tautologies
tautology :: Formula -> Bool
tautology f = all (|= f) (models (fv f)) 

-- satisfiable formulae
satisfiable :: Formula -> Bool
satisfiable f = any (|= f) (models (fv f))

-- contradictions
contradiction :: Formula -> Bool
contradiction = not . satisfiable

-- test cases
logic_correct 
  =   all tautology
      [  (p :||: Not p)
      ,  (p :=>: q) .<=>. (Not p :||: q)
      ,  (p :&&: Not p) :=>: (q :&&: Not q)
      ] 
  &&  all satisfiable
      [  p
      ,  p :||: (Not p :&&: q)
      ]
  &&  all contradiction
      [  p :&&: Not p 
      ,  (p .<=>. q) :&&: p :&&: Not q
      ]
  where 
    f .<=>. g = (f :=>: g) :&&: (g :=>: f)
    p = Var "p"
    q = Var "q"
      
-- -------------------
-- conditional parsing
-- -------------------

-- tokens
type Token
  =  Char

-- variability-aware token stream
type Tokens 
  =  [(Formula, Token)]

-- skipping over unrelated tokens
skip :: Formula -> Tokens -> Tokens
skip ctx [] = []
skip ctx (tts@((pc, t) : ts)) 
  | tautology (ctx :=>: Not pc)  =  skip ctx ts
  | otherwise                    =  tts

-- parser result
data Result alpha
  =  Succ   alpha Tokens 
  |  Fail   String
  |  Split  Formula (Result alpha) (Result alpha)
  deriving Show

-- fold over a parser result
foldResult
  ::  (alpha -> Tokens -> beta)         -- handle Succ
  ->  (String -> beta)                  -- handle Fail
  ->  (Formula -> beta -> beta -> beta) -- handle Split
  ->  Result alpha                      -- parser result to fold over
  ->  beta                              -- overall result

foldResult succ fail split = go where
  go (Succ x ts)   =  succ x ts
  go (Fail m)      =  fail m
  go (Split f r s) =  split f (go r) (go s)

-- fold over a parser result and keep track of the Formula
foldResult'
  ::  (Formula -> alpha -> Tokens -> beta)         -- handle Succ
  ->  (Formula -> String -> beta)                  -- handle Fail
  ->  (Formula -> Formula -> beta -> beta -> beta) -- handle Split
  ->  Result alpha                                 -- parser result to fold over
  ->  Formula                                      -- current Formula
  ->  beta                                         -- overall result
 
foldResult' succ fail split = foldResult succ' fail' split' where
  succ' x ts = \ctx -> succ ctx x ts
  fail' m = \ctx -> fail ctx m
  split' f r s = \ctx -> split ctx f (r (ctx :&&: f)) (s (ctx :&&: Not f))

-- parser
type Parser alpha
  =  Formula -> Tokens -> Result alpha
  
-- basic parser: always succeeds
succeed :: alpha -> Parser alpha
succeed x ctx ts = Succ x ts
  
-- basic parser: accepts the next token and eagerly skip
token :: Parser Token
token ctx []
  =  Fail  "unexpected end of token stream"
token ctx (tts@((pc, t) : ts))
  |  tautology  (ctx :=>: pc)      =  Succ  t (skip ctx ts)
  |  tautology  (ctx :=>: Not pc)  =  token ctx ts
  |  otherwise                     =  Split pc 
                                        (token (ctx :&&: pc) tts)
                                        (token (ctx :&&: Not pc) tts)

-- basic combinator: add a condition to a parser
check :: (alpha -> Bool) -> String -> Parser alpha -> Parser alpha
check c m p ctx ts = foldResult succ Fail Split (p ctx ts) where
  succ x ts | c x = Succ x ts | otherwise = Fail "check failed"

-- derived combinator: accept a token if it satisfies some condition
satisfies :: (Token -> Bool) -> Parser Token
satisfies c = joinFailed (check c "unexpected token" token)

-- basic combinator: sequence of two parsers
(<->) :: Parser alpha -> Parser beta -> Parser (alpha, beta)
(p <-> q) ctx ts = foldResult' succL (const Fail) (const Split) (p ctx ts) ctx where
  succL ctx x ts = foldResult (succR x) Fail Split (q ctx ts)
  succR x y ts = Succ (x, y) ts

-- basic combinator: alternative of two parsers
(<|>) :: Parser alpha -> Parser alpha -> Parser alpha
(p <|> q) ctx ts = foldResult' (const Succ) fail (const Split) (p ctx ts) ctx where
  fail ctx m = q ctx ts 

-- basic combinator: map a function over a parser
(<$>) :: (alpha -> beta) -> Parser alpha -> Parser beta
(h <$> p) ctx ts = foldResult succ Fail Split (p ctx ts) where
  succ x ts = Succ (h x) ts

-- derived combinator: lifted application
(<*>) :: Parser (alpha -> beta) -> Parser alpha -> Parser beta
p <*> q = uncurry ($) <$> (p <-> q)

-- basic combinator: try to join successful split parsers
join :: (Formula -> alpha -> alpha -> alpha) -> Parser alpha -> Parser alpha
join choice p ctx ts = foldResult Succ Fail split (p ctx ts) where
  split f  (Succ x ts)  (Succ y ts')  |  ts == ts'  =  Succ (choice f x y) ts
  split f r s = Split f r s
  
-- basic combinator: try to join failed split parsers
joinFailed :: Parser alpha -> Parser alpha
joinFailed p ctx ts = foldResult Succ Fail split (p ctx ts) where
  split f  (Fail m)  (Fail m')  =  Fail (m ++ ", " ++ m')
  split f r s = Split f r s

-- a symbol for join
choice <&> p = join choice p

-- derived combinator: repetition
many :: Parser alpha -> Parser [alpha]
many p = some p <|> succeed []

some :: Parser alpha -> Parser [alpha]
some p = (:) <$> p <*> many p
 
--type Parser alpha
--  =  Formula -> Tokens -> Result alpha

repOpt :: Parser alpha -> Parser [(Formula, alpha)]
repOpt p = mconcat <$> many (opt p)
opt :: Parser alpha -> Parser [(Formula, alpha)]
--repOpt p = foldr (++) [] <$> many (opt (:[]) (\formula thenB elseB -> [(formula, thenB), (formula, elseB)]) p)
--repOpt p = many (opt (:[]) (\formula thenB elseB -> [(formula, thenB), (formula, elseB)]) p)
--opt = undefined :: Parser a -> Parser [a]
--opt :: Monoid b => (Formula -> a -> b) -> (Formula -> b -> b -> b) -> Parser a -> Parser b
--opt = undefined
--opt singleton choice p ctx ts =
--  case ts of
--    [] -> Fail ""
--    (pc, t) : rest
--      | tautology  (ctx :=>: pc)      -> p ctx ts
--      | tautology  (ctx :=>: Not pc)  -> Succ mempty rest
--      | otherwise -> choice pc
--	  (singleton (ctx :&&: pc) (p (ctx :&&: pc) ts))
--	  (Succ mempty rest)
opt p ctx ts =
  case ts of
    [] -> Fail ""
    (pc, t) : rest
      | tautology  (ctx :=>: pc)      -> (singleton ctx <$> p) ctx ts
      | tautology  (ctx :=>: Not pc)  -> Succ mempty rest -- Here, we prefer skipping to parsing, and it's the only point of this code. 
      | otherwise -> choice pc
	(opt p (ctx :&&: pc) ts)
	(Succ mempty rest)
  where
    singleton :: Formula -> alpha -> [(Formula, alpha)]
    singleton ctx res = [(ctx, res)]
    choice :: Formula -> Result alpha -> Result alpha -> Result alpha
    choice pc = Split pc

-- derived combinator: sequence and ignore
(<*) :: Parser alpha -> Parser beta -> Parser alpha
p <* q = fst <$> (p <-> q)

(*>) :: Parser alpha -> Parser beta -> Parser beta
p *> q = snd <$> (p <-> q)

-- ----------------------------
-- Variability-aware repetition
-- ----------------------------

-- many' 
--   ::  (alpha -> alpha -> alpha)             -- append node
--   ->  (Formula -> alpha -> alpha -> alpha)  -- choice node
--   ->  alpha                                 -- empty result
--   ->  Parser alpha                          -- parser for single element 
--   ->  Parser (VarList alpha)                -- parser for some elements
--   
-- many' append choice empty p ctx ts 
--   = join   

-- variability-aware AST
data VWord
  =  Word String
  |  Choice Formula VWord VWord
  deriving Show

-- parser for a single letter
letter :: Parser Char
letter = satisfies isLetter
  
-- parser for a single whitespace character
space :: Parser Char
space = satisfies isSpace

-- parser for a couple of spaces
spaces :: Parser String
spaces = many space

-- parser for a single word
vWord :: Parser VWord
vWord = Choice <&> (Word <$> some letter)

-- parser for many words
vWords :: Parser [VWord]
vWords = spaces *> many (vWord <* spaces)

-- example token stream
tokens :: Tokens
tokens 
  = [ (common    , 'H')
    , (english   , 'e')
    , (german    , 'a')
    , (common    , 'l')
    , (common    , 'l')
    , (common    , 'o')
    , (common    , ' ')
    , (english   , 'm')
    , (english   , 'y')
    , (common    , ' ')
    , (common    , 'W')
    , (english   , 'o')
    , (german    , 'e')
    , (english   , 'r')
    , (common    , 'l')
    , (german    , 't')
    , (english   , 'd')
    ]
    
english = Var "english"
german = Var "german"
common = english :||: german

-- we need a feature model here
fm = english `xor` german
xor f g = (f :||: g) :&&: Not (f :&&: g)

test = vWords fm tokens

-- repopt example

-- features
yes, no, both  ::  Formula
yes   =  Var "selected"
no    =  Not (Var "selected")
both  =  yes :||: no

-- annoying but realistic token stream
annoying :: Tokens
annoying
  =  [ (yes , '(')
     , (no  , '1')
     , (no  , '(')
     , (both, '2')
     , (both, ')')
     ]



